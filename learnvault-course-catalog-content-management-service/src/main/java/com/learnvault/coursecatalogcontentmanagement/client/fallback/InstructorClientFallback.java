package com.learnvault.coursecatalogcontentmanagement.client.fallback;

import com.learnvault.coursecatalogcontentmanagement.client.InstructorClient;
import com.learnvault.coursecatalogcontentmanagement.client.dto.InstructorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Fallback for {@link InstructorClient}.
 *
 * <p>When the instructor-session-management-service is unavailable (the circuit
 * breaker is open or the call fails), Spring Cloud OpenFeign routes calls here
 * so the application degrades gracefully instead of throwing an exception.</p>
 */
@Slf4j
@Component
public class InstructorClientFallback implements InstructorClient {

    @Override
    public InstructorDto getInstructorById(Integer id) {
        log.warn("Fallback: unable to fetch instructor with id={} from instructor-session-management-service", id);
        return null;
    }
}
