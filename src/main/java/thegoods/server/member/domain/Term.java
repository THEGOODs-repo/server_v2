package thegoods.server.member.domain;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import thegoods.server.common.domain.BaseDateTimeEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Term extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "term_id")
    private Long id;


    @Column(columnDefinition = "VARCHAR(30)")
    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean essential;

    @OneToMany(mappedBy = "term", cascade = CascadeType.ALL)
    private List<MemberTerm> memberTermList = new ArrayList<>();
}
