package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JhipsterVue2App;

import com.mycompany.myapp.domain.JHIPermission;
import com.mycompany.myapp.repository.JHIPermissionRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JHIPermissionResource REST controller.
 *
 * @see JHIPermissionResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JhipsterVue2App.class)
public class JHIPermissionResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    @Autowired
    private JHIPermissionRepository jHIPermissionRepository;

    @Mock
    private JHIPermissionRepository jHIPermissionRepositoryMock;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restJHIPermissionMockMvc;

    private JHIPermission jHIPermission;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JHIPermissionResource jHIPermissionResource = new JHIPermissionResource(jHIPermissionRepository);
        this.restJHIPermissionMockMvc = MockMvcBuilders.standaloneSetup(jHIPermissionResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static JHIPermission createEntity(EntityManager em) {
        JHIPermission jHIPermission = new JHIPermission()
            .name(DEFAULT_NAME)
            .url(DEFAULT_URL);
        return jHIPermission;
    }

    @Before
    public void initTest() {
        jHIPermission = createEntity(em);
    }

    @Test
    @Transactional
    public void createJHIPermission() throws Exception {
        int databaseSizeBeforeCreate = jHIPermissionRepository.findAll().size();

        // Create the JHIPermission
        restJHIPermissionMockMvc.perform(post("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jHIPermission)))
            .andExpect(status().isCreated());

        // Validate the JHIPermission in the database
        List<JHIPermission> jHIPermissionList = jHIPermissionRepository.findAll();
        assertThat(jHIPermissionList).hasSize(databaseSizeBeforeCreate + 1);
        JHIPermission testJHIPermission = jHIPermissionList.get(jHIPermissionList.size() - 1);
        assertThat(testJHIPermission.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJHIPermission.getUrl()).isEqualTo(DEFAULT_URL);
    }

    @Test
    @Transactional
    public void createJHIPermissionWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = jHIPermissionRepository.findAll().size();

        // Create the JHIPermission with an existing ID
        jHIPermission.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJHIPermissionMockMvc.perform(post("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jHIPermission)))
            .andExpect(status().isBadRequest());

        // Validate the JHIPermission in the database
        List<JHIPermission> jHIPermissionList = jHIPermissionRepository.findAll();
        assertThat(jHIPermissionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJHIPermissions() throws Exception {
        // Initialize the database
        jHIPermissionRepository.saveAndFlush(jHIPermission);

        // Get all the jHIPermissionList
        restJHIPermissionMockMvc.perform(get("/api/jhi-permissions?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jHIPermission.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())));
    }
    

    @Test
    @Transactional
    public void getJHIPermission() throws Exception {
        // Initialize the database
        jHIPermissionRepository.saveAndFlush(jHIPermission);

        // Get the jHIPermission
        restJHIPermissionMockMvc.perform(get("/api/jhi-permissions/{id}", jHIPermission.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jHIPermission.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingJHIPermission() throws Exception {
        // Get the jHIPermission
        restJHIPermissionMockMvc.perform(get("/api/jhi-permissions/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJHIPermission() throws Exception {
        // Initialize the database
        jHIPermissionRepository.saveAndFlush(jHIPermission);

        int databaseSizeBeforeUpdate = jHIPermissionRepository.findAll().size();

        // Update the jHIPermission
        JHIPermission updatedJHIPermission = jHIPermissionRepository.findById(jHIPermission.getId()).get();
        // Disconnect from session so that the updates on updatedJHIPermission are not directly saved in db
        em.detach(updatedJHIPermission);
        updatedJHIPermission
            .name(UPDATED_NAME)
            .url(UPDATED_URL);

        restJHIPermissionMockMvc.perform(put("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedJHIPermission)))
            .andExpect(status().isOk());

        // Validate the JHIPermission in the database
        List<JHIPermission> jHIPermissionList = jHIPermissionRepository.findAll();
        assertThat(jHIPermissionList).hasSize(databaseSizeBeforeUpdate);
        JHIPermission testJHIPermission = jHIPermissionList.get(jHIPermissionList.size() - 1);
        assertThat(testJHIPermission.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJHIPermission.getUrl()).isEqualTo(UPDATED_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingJHIPermission() throws Exception {
        int databaseSizeBeforeUpdate = jHIPermissionRepository.findAll().size();

        // Create the JHIPermission

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restJHIPermissionMockMvc.perform(put("/api/jhi-permissions")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(jHIPermission)))
            .andExpect(status().isBadRequest());

        // Validate the JHIPermission in the database
        List<JHIPermission> jHIPermissionList = jHIPermissionRepository.findAll();
        assertThat(jHIPermissionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteJHIPermission() throws Exception {
        // Initialize the database
        jHIPermissionRepository.saveAndFlush(jHIPermission);

        int databaseSizeBeforeDelete = jHIPermissionRepository.findAll().size();

        // Delete the jHIPermission
        restJHIPermissionMockMvc.perform(delete("/api/jhi-permissions/{id}", jHIPermission.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<JHIPermission> jHIPermissionList = jHIPermissionRepository.findAll();
        assertThat(jHIPermissionList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(JHIPermission.class);
        JHIPermission jHIPermission1 = new JHIPermission();
        jHIPermission1.setId(1L);
        JHIPermission jHIPermission2 = new JHIPermission();
        jHIPermission2.setId(jHIPermission1.getId());
        assertThat(jHIPermission1).isEqualTo(jHIPermission2);
        jHIPermission2.setId(2L);
        assertThat(jHIPermission1).isNotEqualTo(jHIPermission2);
        jHIPermission1.setId(null);
        assertThat(jHIPermission1).isNotEqualTo(jHIPermission2);
    }
}
