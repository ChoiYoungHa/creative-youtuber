package workoutprj.workout.Service.Impl;

import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import workoutprj.workout.Domain.Member;
import workoutprj.workout.Repositrory.MemberRepository;
import workoutprj.workout.Service.IMemberService;

@Service
@RequiredArgsConstructor
public class MemberService implements IMemberService {

    private final MemberRepository memberRepository;

    // 회원가입
    @Override
    public void join(Member member) {
        memberRepository.joinMember(member);
    }

    // 로그인
    @Override
    public Member login(String email, String password) {
        return memberRepository.memberLogin(email, password);
    }


}
