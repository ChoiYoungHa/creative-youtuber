package creativeprj.creative.Domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID")
    private Long member_id;

    @OneToMany(mappedBy = "member")
    private List<Board> boards = new ArrayList<>();

    private String user_name;
    private String password;
    private String nickname;
    private String email;
}
