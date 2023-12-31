package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:test-data.sql"),
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:clean-data.sql")
})
public class StatsServerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetStats() throws Exception {

        String url = "/stats";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("start", "2021-01-01 00:00:00");
        params.add("end", "2100-01-01 00:00:00");
        params.add("uris", "test/1");
        params.add("unique", "false");

        mockMvc.perform(get(url)
                        .params(params))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].app").value("ewm-test1"))
                .andExpect(jsonPath("$[0].uri").value("test/1"))
                .andExpect(jsonPath("$[0].hits").value(2));
    }

    @Test
    public void testPostEvent() throws Exception {
        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"app\": \"ewm-test1\", \"ip\": \"3.3.3.3\", \"uri\": \"test/1\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(4));
    }

}
