package workoutprj.workout.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOARD_ID")
    private Long board_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;


    private String board_name;
    private String board_content;
    private int board_click;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    public void incrementClick() {
        this.board_click += 1;  // 조회수를 1 증가시킴
    }

    // 양방향 편의 메서드
    public void changeMember(Member member) {
        this.member = member;
        member.getBoards().add(this);
    }




}
