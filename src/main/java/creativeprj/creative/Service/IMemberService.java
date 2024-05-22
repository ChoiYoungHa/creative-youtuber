package creativeprj.creative.Service;

import creativeprj.creative.Domain.Member;

public interface IMemberService {

    void join(Member member);
    Member login(String email, String password);
}
