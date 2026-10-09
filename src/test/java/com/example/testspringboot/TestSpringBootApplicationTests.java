package com.example.testspringboot;

import com.example.testspringboot.model.User;
import com.example.testspringboot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TestSpringBootApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void createRejectsInvalidDataAndDuplicateName() throws Exception {
        mockMvc.perform(post("/users/save")
                        .param("name", "")
                        .param("age", "")
                        .param("salary", ""))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Name is required")))
                .andExpect(content().string(containsString("Age is required")))
                .andExpect(content().string(containsString("Salary is required")));

        MvcResult created = mockMvc.perform(post("/users/save")
                        .param("name", "Bob")
                        .param("age", "30")
                        .param("salary", "12000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "User created successfully"))
                .andReturn();

        mockMvc.perform(get("/").flashAttrs(created.getFlashMap()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User created successfully")))
                .andExpect(content().string(containsString("Bob")));

        mockMvc.perform(post("/users/save")
                        .param("name", "Bob")
                        .param("age", "67")
                        .param("salary", "67000"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("A User with name Bob already exist")));
    }

    @Test
    void searchEditAndDeleteUser() throws Exception {
        User silvia = new User();
        silvia.setName("Silvia");
        silvia.setAge(42);
        silvia.setSalary(15000L);
        silvia = userRepository.save(silvia);

        mockMvc.perform(get("/").param("keyword", "Silvia"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Silvia")))
                .andExpect(content().string(containsString("Found")));

        mockMvc.perform(get("/").param("keyword", "nobody-xyz"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No users found.")));

        mockMvc.perform(post("/users/save")
                        .param("id", silvia.getId().toString())
                        .param("name", "Silvia")
                        .param("age", "43")
                        .param("salary", "18000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "User updated successfully"));

        mockMvc.perform(get("/users/edit/" + silvia.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Update")))
                .andExpect(content().string(containsString("18000")));

        mockMvc.perform(post("/users/delete/" + silvia.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("success", "User deleted successfully"));
    }
}
