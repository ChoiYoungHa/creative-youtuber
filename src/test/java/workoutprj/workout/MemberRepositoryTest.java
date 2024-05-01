package workoutprj.workout;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import workoutprj.workout.Domain.Member;
import workoutprj.workout.Repositrory.MemberRepository;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;


    /**
     * 회원 가입 테스트
     */

    @Test
    public void 회원_가입(){
        //given
        Member member = new Member();
        member.setEmail("123@naver.com");
        member.setPassword("password");


        //when
        memberRepository.joinMember(member);

        //then
        Assert.assertEquals(member, memberRepository.findOne(member.getMember_id()));
    }


    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void 회원_가입_중복예외(){
        //given
        Member member = new Member();
        member.setEmail("123@naver.com");
        member.setPassword("password");

        Member member1 = new Member();
        member1.setEmail("123@naver.com");
        member1.setPassword("password");


        //when
        memberRepository.joinMember(member);
        memberRepository.joinMember(member1);

        //then
        Assert.fail();
    }


    /**
     * 로그인 테스트
     */

    @Test
    public void  로그인_테스트(){
        //given
        Member member = new Member();
        member.setEmail("123@naver.com");
        member.setPassword("password");
        memberRepository.joinMember(member);

        //when
        Member loginMember = memberRepository.memberLogin("123@naver.com", "password");

        //then
        Assert.assertEquals(member.getMember_id(), loginMember.getMember_id());
    }
}
