package workoutprj.workout.Service;

import workoutprj.workout.Domain.Member;

public interface IMemberService {

    void join(Member member);
    Member login(String email, String password);
}
