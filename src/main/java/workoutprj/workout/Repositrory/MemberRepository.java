package workoutprj.workout.Repositrory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import workoutprj.workout.Domain.Member;
import workoutprj.workout.Exception.EmailAlreadyExistsException;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberRepository {

    private final EntityManager em;


    @Transactional
    public void joinMember(Member member) {
        if (!emailValidate(member.getEmail())) {
            em.persist(member);
        } else {
            throw new EmailAlreadyExistsException("이미 존재하는 회원입니다.");
        }
    }


    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }


    // 회원가입 중복 검사
    private boolean emailValidate(String email) {
        List<Member> result = em.createQuery("select m from Member as m where m.email = :email", Member.class)
                .setParameter("email", email)
                .getResultList();
        return !result.isEmpty();
    }

    // 로그인
    public Member memberLogin(String email, String password) {
        // 이메일과 비밀번호 일치하는 회원 조회, 없으면 로그인 실패
        try{
            return em.createQuery("select m from Member as m where m.email = :email and m.password = :password", Member.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        }catch (NoResultException e){
            throw new IllegalStateException("잘못된 이메일 또는 비밀번호입니다.");
        }
    }

}
