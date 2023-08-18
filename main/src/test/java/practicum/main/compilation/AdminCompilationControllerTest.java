/*package practicum.main.compilation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.main.compilation.controller.AdminCompilationController;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.service.CompilationService;
import ru.practicum.main.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminCompilationController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdminCompilationControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    CompilationService compilationService;
    @Autowired
    private MockMvc mvc;
    NewCompilationDto testNewCompilationDto;
    Compilation testCompilation;
    CompilationDto testCompilationDto;

    @BeforeEach
    public void setUp() {
        testNewCompilationDto = new NewCompilationDto("test", null, new HashSet<>());
        testCompilation = new Compilation(1L, testNewCompilationDto.getTitle(), testNewCompilationDto.getPinned(), new HashSet<>());
        testCompilationDto = new CompilationDto(testCompilation.getId(), testCompilation.getTitle(), testCompilation.getPinned(),
                new ArrayList<>());
    }

    @Test
    public void create_whenInvokedWithCorrectData_thenReturnStatusOk() throws Exception {
        when(compilationService.create(any()))
                .thenReturn(testCompilationDto);

        mvc.perform(post("/compilations")
                        .content(mapper.writeValueAsString(testCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testCompilationDto.getId()), Long.class))
                .andExpect(jsonPath("$.email", is(testCompilationDto.getTitle()), String.class))
                .andExpect(jsonPath("$.name", is(testCompilationDto.getPinned()), Boolean.class));
    }
}*/
