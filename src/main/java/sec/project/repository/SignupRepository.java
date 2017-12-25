package sec.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import sec.project.domain.Signup;

public interface SignupRepository extends JpaRepository<Signup, Long> {
    
    
    Signup findByName(String name);
    Signup findByPassword(String password);
}
