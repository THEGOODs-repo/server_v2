package thegoods.server.member.domain.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import thegoods.server.member.domain.Term;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findById(Long id);
}
