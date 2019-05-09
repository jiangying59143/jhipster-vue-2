package com.mycompany.myapp.web.rest;
import com.mycompany.myapp.domain.JHIPermission;
import com.mycompany.myapp.repository.JHIPermissionRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import com.mycompany.myapp.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing JHIPermission.
 */
@RestController
@RequestMapping("/api")
public class JHIPermissionResource {

    private final Logger log = LoggerFactory.getLogger(JHIPermissionResource.class);

    private static final String ENTITY_NAME = "jHIPermission";

    private final JHIPermissionRepository jHIPermissionRepository;

    public JHIPermissionResource(JHIPermissionRepository jHIPermissionRepository) {
        this.jHIPermissionRepository = jHIPermissionRepository;
    }

    /**
     * POST  /jhi-permissions : Create a new jHIPermission.
     *
     * @param jHIPermission the jHIPermission to create
     * @return the ResponseEntity with status 201 (Created) and with body the new jHIPermission, or with status 400 (Bad Request) if the jHIPermission has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/jhi-permissions")
    public ResponseEntity<JHIPermission> createJHIPermission(@RequestBody JHIPermission jHIPermission) throws URISyntaxException {
        log.debug("REST request to save JHIPermission : {}", jHIPermission);
        if (jHIPermission.getId() != null) {
            throw new BadRequestAlertException("A new jHIPermission cannot already have an ID", ENTITY_NAME, "idexists");
        }
        JHIPermission result = jHIPermissionRepository.save(jHIPermission);
        return ResponseEntity.created(new URI("/api/jhi-permissions/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /jhi-permissions : Updates an existing jHIPermission.
     *
     * @param jHIPermission the jHIPermission to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated jHIPermission,
     * or with status 400 (Bad Request) if the jHIPermission is not valid,
     * or with status 500 (Internal Server Error) if the jHIPermission couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/jhi-permissions")
    public ResponseEntity<JHIPermission> updateJHIPermission(@RequestBody JHIPermission jHIPermission) throws URISyntaxException {
        log.debug("REST request to update JHIPermission : {}", jHIPermission);
        if (jHIPermission.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        JHIPermission result = jHIPermissionRepository.save(jHIPermission);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, jHIPermission.getId().toString()))
            .body(result);
    }

    /**
     * DELETE  /jhi-permissions/:id : delete the "id" jHIPermission.
     *
     * @param id the id of the jHIPermission to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/jhi-permissions/{id}")
    public ResponseEntity<Void> deleteJHIPermission(@PathVariable Long id) {
        log.debug("REST request to delete JHIPermission : {}", id);
        jHIPermissionRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
