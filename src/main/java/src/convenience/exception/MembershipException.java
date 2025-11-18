package src.convenience.exception;

public class MembershipException extends RuntimeException {

    public MembershipException() {
        super("멤버십 적용 여부가 선택되지 않았습니다.");
    }
}