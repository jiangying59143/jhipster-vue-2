package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.JHIPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the JHIPermission entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JHIPermissionRepository extends JpaRepository<JHIPermission, Long> {

}
