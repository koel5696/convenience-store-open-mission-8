var currentPromoData = null;
var promoQueue = [];   // ⭐ 프로모션/재고 이슈를 하나씩 처리하기 위한 큐

document.addEventListener("DOMContentLoaded", function () {
    loadProducts();
    loadCart();
});

var allProducts = {};
var currentPayRequest = {
    missingPromotion: null,
    insufficientStock: null,
    membership: null
};

async function loadProducts() {
    try {
        var res = await fetch("/api/products");
        var products = await res.json();
        var container = document.getElementById("product-list");
        container.innerHTML = "";

        for (var i = 0; i < products.length; i++) {
            var p = products[i];
            allProducts[p.id] = p;

            /* ⭐ 프로모션 블록 (2줄 구조) */
            var promoHtml = "";
            if (p.promotion !== "null") {

                var promoClass = "";
                var promoLabel = p.promotion;

                var isLimited = promoLabel.includes("한정");
                var purePromo = promoLabel.replace("한정", "").trim();

                if (isLimited) promoClass = "promo-badge-limited";
                else if (purePromo === "1+1") promoClass = "promo-badge-11";
                else if (purePromo === "2+1") promoClass = "promo-badge-21";

                var start = "";
                var end = "";
                if (p.promotionDate !== null) {
                    start = p.promotionDate.start.substring(2).replace(/-/g, "/");
                    end = p.promotionDate.end.substring(2).replace(/-/g, "/");
                }

                var dateText = "";
                if (start && end) {
                    dateText =
                        '<div class="promo-date-text">📅 ' +
                        start + ' ~ ' + end +
                        '</div>';
                }

                promoHtml =
                    '<div class="promo-block">' +
                    '   <div class="promo-badge-row">' +
                    '       <span class="' + promoClass + '">' + purePromo + '</span>' +
                    '   </div>' +
                    '   <div class="promo-date-row">' +
                    dateText +
                    '   </div>' +
                    '</div>';
            }

            /* ⭐ 재고, 버튼 처리 */
            var isOutOfStock = p.quantity === 0;
            var stockColor = isOutOfStock ? "red" : "#888";
            var buttonText = isOutOfStock ? "품절" : "담기";
            var disabledAttr = isOutOfStock ? "disabled" : "";

            /* ⭐ 최종 HTML */
            var html = ""
                + '<div class="card">'
                + promoHtml                          // ← 프로모션/기간 위치
                + "<h3>" + p.name + "</h3>"
                + "<p>" + p.price.toLocaleString() + "원</p>"
                + '<p style="color:' + stockColor + '">재고: ' + p.quantity + "개</p>"
                + '<button class="btn" onclick="addToCart(' + p.id + ')" ' + disabledAttr + ">"
                + buttonText
                + "</button>"
                + "</div>";

            container.insertAdjacentHTML("beforeend", html);
        }
    } catch (e) {
        console.error("상품 로딩 실패:", e);
        alert("상품을 불러오지 못했습니다.");
    }
}


// 장바구니 담기
async function addToCart(productId, quantity) {
    var q = quantity;
    if (!q) {
        q = 1;
    }

    try {
        var res = await fetch("/api/cart/add", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({productId: productId, quantity: q})
        });

        if (!res.ok) {
            // 서버에서 온 에러 메시지를 받는다
            var errorData = await res.json().catch(() => null);

            if (errorData && typeof errorData === "string") {
                alert(errorData); // ← 서버가 String만 보내므로 그대로 출력
            } else if (errorData && errorData.message) {
                alert(errorData.message);
            } else {
                alert("담기 실패: 재고를 초과했습니다.");
            }
            return;
        }

        await loadCart();
    } catch (e) {
        console.error(e);
        alert("장바구니 담기 중 통신 오류가 발생했습니다.");
    }
}

async function removeItem(productId) {
    try {
        var res = await fetch("/api/cart/remove", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({productId: productId})
        });

        if (!res.ok) {
            alert("상품 제거 실패");
            return;
        }

        await loadCart();
        await loadProducts();
    } catch (e) {
        console.error(e);
        alert("서버 통신 오류");
    }
}


// 장바구니 조회
async function loadCart() {
    try {
        var res = await fetch("/api/cart");
        var items = await res.json();

        var container = document.getElementById("cart-list");
        container.innerHTML = "";
        var totalCount = 0;
        var keys = Object.keys(items);

        if (keys.length === 0) {
            container.innerHTML = "<li>장바구니가 비었습니다.</li>";
        } else {
            for (var i = 0; i < keys.length; i++) {
                var id = keys[i];
                var qty = items[id];
                var product = allProducts[id];
                var name = "상품 " + id;

                if (product) {
                    name = product.name;
                }

                var li = ""
                    + '<li class="cart-item">'
                    + "  <span>" + name + "</span>"
                    + "  <span><b>" + qty + "</b>개</span>"
                    + '  <button class="btn cancel-btn" style="margin-left:10px;" onclick="removeItem(' + id + ')">제거</button>'
                    + "</li>";

                container.insertAdjacentHTML("beforeend", li);
                totalCount = totalCount + qty;
            }
        }

        var totalEl = document.getElementById("cart-total-count");
        totalEl.innerText = "총 " + totalCount + "개";
    } catch (e) {
        console.error(e);
        alert("장바구니를 불러오지 못했습니다.");
    }
}

// 결제 시작
function startPayment() {
    currentPayRequest = {
        missingPromotion: null,
        insufficientStock: null,
        membership: null
    };
    currentPromoData = null;
    promoQueue = [];   // ⭐ 이전 결제에서 남은 이슈 초기화
    requestCheckout();
}


// 결제 요청
async function requestCheckout() {
    try {
        var res = await fetch("/api/pay", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(currentPayRequest)
        });

        if (res.status === 200) {
            var receipt = await res.json();
            showReceipt(receipt);
            await loadProducts();
            await loadCart();
            closeModal();
            return;
        }

        if (res.status === 409) {
            var data = await res.json();
            console.log("409 응답:", data);

            if (data.type === "EMPTY_CART") {
                alert(data.message); // 장바구니가 비어 있습니다.
                return;
            }

            // 멤버십 질문
            if (data.type && typeof data.type === "string") {
                var upper = data.type.trim().toUpperCase();
                if (upper === "MEMBERSHIP") {
                    showMembershipModal();
                    return;
                }
            }

            promoQueue = [];

            if (Array.isArray(data.missingPromotionItems)) {
                for (var i = 0; i < data.missingPromotionItems.length; i++) {
                    var u = data.missingPromotionItems[i];
                    promoQueue.push({
                        type: "UPSELL",
                        productName: u.productName,
                        promotion: u.promotion
                    });
                }
            }

            if (Array.isArray(data.stockIssues)) {
                for (var j = 0; j < data.stockIssues.length; j++) {
                    var s = data.stockIssues[j];
                    promoQueue.push({
                        type: "STOCK",
                        productName: s.productName,
                        promotion: s.promotion
                    });
                }
            }

            openNextPromoIssue();  // ⭐ 첫 번째 이슈부터 처리 시작
            return;
        }


        if (res.status === 400) {
            await res.json().catch(function () {
            });
            alert("결제에 실패했습니다. 다시 시도해주세요.");
            return;
        }

        alert("알 수 없는 오류가 발생했습니다. 상태 코드: " + res.status);
    } catch (e) {
        console.error(e);
        alert("결제 요청 중 서버 통신 오류가 발생했습니다.");
    }
}

function openNextPromoIssue() {
    if (promoQueue.length === 0) {
        if (currentPromoData && currentPromoData.type === "UPSELL") {
            currentPayRequest.missingPromotion = true;
        }

        if (currentPromoData && currentPromoData.type === "STOCK") {
            currentPayRequest.insufficientStock = true;
        }

        requestCheckout();
        return;
    }

    currentPromoData = promoQueue.shift();
    openPromoModal(currentPromoData);
}


// 프로모션 / 재고 안내 모달 열기
function openPromoModal(issue) {
    var modal = document.getElementById("promo-modal");
    var list = document.getElementById("promo-list");
    var title = document.getElementById("promo-modal-title");
    list.innerHTML = "";

    var msg = "";
    var headerText = "";

    if (issue.type === "UPSELL") {
        headerText = "프로모션 추가 혜택 안내";
        msg = issue.productName
            + " 상품은 " + issue.promotion
            + " 프로모션으로 1개를 무료로 증정을 받으실 수 있습니다. 받으시겠습니까?";
    } else {
        headerText = "프로모션 재고 부족 안내";
        msg = issue.productName
            + " 상품은 " + issue.promotion
            + " 프로모션 상품이지만, 무료 증정을 위한 재고가 부족합니다. "
            + "증정 없이 결제를 진행하시겠습니까?";
    }

    title.innerText = headerText;

    var boxClass = "promotion-item";
    if (issue.type === "STOCK") {
        boxClass = "stock-issue-item";
    }

    var html = ""
        + '<div class="' + boxClass + '">'
        + "<div>" + msg + "</div>"
        + "</div>";

    list.innerHTML = html;

    var confirmBtn = document.getElementById("promo-confirm-btn");
    if (issue.type === "UPSELL") {
        confirmBtn.innerText = "예 (추가 담기)";
    } else {
        confirmBtn.innerText = "예 (그대로 결제)";
    }

    modal.style.display = "flex";
}


// 모달 "예 / 아니요" 버튼 처리
async function handleModalResponse(userClickedYes) {
    var issue = currentPromoData;   // 현재 처리 중인 단일 이슈

    if (!issue) {
        closeModal();
        return;
    }

    if (userClickedYes) {
        // ✅ "예" 선택
        if (issue.type === "UPSELL") {
            // 업셀인 경우: 해당 상품 1개 장바구니 추가
            var productId = findProductIdByNameAndPromotion(issue.productName, issue.promotion);
            if (productId !== null) {
                await addToCart(productId, 1);
            }
        }
        // STOCK 인 경우 "예"는 그냥 증정 없이 진행 → 별도 처리 없음

        closeModal();
        openNextPromoIssue();   // 다음 이슈로 진행
        return;
    }

    // ❌ "아니요" 선택
    if (issue.type === "STOCK") {
        // 재고 부족인데 "아니요"면: 결제 자체 취소
        closeModal();
        alert("증정이 불가능하여 결제를 취소했습니다.");

        currentPayRequest = {
            missingPromotion: null,
            insufficientStock: null,
            membership: null
        };
        promoQueue = [];
        currentPromoData = null;
        return;
    }

    // 업셀에 대해 "아니요": 해당 상품은 혜택만 포기, 다음 이슈로
    closeModal();
    openNextPromoIssue();
}


// 이름 + 프로모션으로 productId 역조회
function findProductIdByNameAndPromotion(name, promo) {
    var ids = Object.keys(allProducts);
    for (var i = 0; i < ids.length; i++) {
        var id = ids[i];
        var product = allProducts[id];
        if (!product) {
            continue;
        }

        if (product.name === name && product.promotion === promo) {
            return Number(id);
        }
    }
    return null;
}

function showMembershipModal() {
    document.getElementById("membership-modal").classList.add("show");
}

async function handleMembership(isApply) {
    document.getElementById("membership-modal").classList.remove("show");
    currentPayRequest.membership = isApply;
    await requestCheckout();
}

// 모달 닫기
function closeModal() {
    var modal = document.getElementById("promo-modal");
    modal.style.display = "none";
}

// 영수증 출력
function showReceipt(receipt) {
    var area = document.getElementById("receipt-area");
    var content = document.getElementById("receipt-content");
    area.style.display = "block";
    content.innerHTML = "";

    // 유료 구매 상품
    for (var i = 0; i < receipt.items.length; i++) {
        var item = receipt.items[i];
        if (item.paidQuantity > 0) {
            var line = ""
                + '<div class="receipt-row">'
                + "<span>" + item.productName + " (" + item.paidQuantity + "개)</span>"
                + "<span>" + (item.price * item.paidQuantity).toLocaleString() + "원</span>"
                + "</div>";
            content.insertAdjacentHTML("beforeend", line);
        }
    }

    // 증정 상품
    var giftItems = [];
    for (var j = 0; j < receipt.items.length; j++) {
        var gi = receipt.items[j];
        if (gi.giftQuantity > 0) {
            giftItems.push(gi);
        }
    }

    if (giftItems.length > 0) {
        content.insertAdjacentHTML("beforeend",
            '<div style="border-bottom:1px dotted #ccc; margin:5px 0;"></div>');
        content.insertAdjacentHTML("beforeend",
            '<div style="font-size:12px; color:#888; margin-bottom:5px;">[증정 상품]</div>');

        for (var k = 0; k < giftItems.length; k++) {
            var g = giftItems[k];

            // ⭐ 증정품의 실제 금액 (원래 가격 × 개수)
            var giftValue = g.price * g.giftQuantity;

            var gLine = ""
                + '<div class="receipt-row" style="color:#ff6b6b;">'
                + "<span>" + g.productName + " (" + g.giftQuantity + "개)</span>"
                + "<span>" + giftValue.toLocaleString() + "원</span>"
                + "</div>";

            content.insertAdjacentHTML("beforeend", gLine);
        }
    }


    content.insertAdjacentHTML("beforeend", '<div class="total-row"></div>');

    addSummaryRow(content, "총 구매액", receipt.totalPrice, false);
    addSummaryRow(content, "행사 할인", receipt.promotionDiscount, true);
    addSummaryRow(content, "멤버십 할인", receipt.membershipDiscount, true);

    var finalPrice = receipt.totalPrice
        - receipt.promotionDiscount
        - receipt.membershipDiscount;

    var finalRow = ""
        + '<div class="receipt-row" '
        + 'style="font-size:18px; font-weight:bold; margin-top:10px; color:#2ac1bc;">'
        + "<span>최종 결제금액</span>"
        + "<span>" + finalPrice.toLocaleString() + "원</span>"
        + "</div>";

    content.insertAdjacentHTML("beforeend", finalRow);
}

function addSummaryRow(contentEl, label, value, isMinus) {
    var sign = "";
    if (isMinus) {
        sign = "-";
    }

    var row = ""
        + '<div class="receipt-row">'
        + "<span>" + label + "</span>"
        + "<span>" + sign + value.toLocaleString() + "원</span>"
        + "</div>";

    contentEl.insertAdjacentHTML("beforeend", row);
}

// 처음으로
async function resetCart() {
    var area = document.getElementById("receipt-area");
    area.style.display = "none";

    currentPayRequest = {
        missingPromotion: null,
        insufficientStock: null,
        membership: null
    };
    currentPromoData = null;

    await loadCart();
    await loadProducts();
}