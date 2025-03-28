package ku.cs.restaurant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class SampleTest {

    @Mock
    private MyService myService; // Mock object

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // เปิดใช้งาน Mockito
    }

    @Test
    void testMockito() {
        when(myService.getData()).thenReturn("Hello Mockito!");

        String result = myService.getData();
        assertEquals("Hello Mockito!", result);
    }

    interface MyService {
        String getData();
    }
}
