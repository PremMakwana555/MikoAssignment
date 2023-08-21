package ai.miko.assignment.service;

import ai.miko.assignment.repository.RedisRepository;
import ai.miko.assignment.utils.CSVHelperUtil;
import lombok.SneakyThrows;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @InjectMocks
    private TableService tableService;

    @Mock
    private CSVHelperUtil CSVHelperUtil;

    @Mock
    private RedisRepository repository;

    @SneakyThrows
    @Test
    void givenInvalidTypeQuery_whenQueryRun_thenReturnException() {
        // Given Invalid query
        String query = "CREATE TABLE Persons (PersonID in, LastName varchar(255), FirstName varchar(255), Address varchar(255),\n" +
                "    City varchar(255) );";

        // When run query
        ThrowableAssert.ThrowingCallable queryRun = () -> tableService.executeQuery(query);

        // Then throw exception
        assertThatThrownBy(queryRun).hasMessageContaining("Invalid Sql Statement");
    }

    @SneakyThrows
    @Test
    void executeValidTypeQuery() {
        String query = "CREATE TABLE Persons (PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255),\n" +
                "    City varchar(255) );";

        assertDoesNotThrow(() -> tableService.executeQuery(query));
    }

    @SneakyThrows
    @Test
    void createTypeQuery() {
        String query = "CREATE TABLE Persons (PersonID int, LastName varchar(255), FirstName varchar(255), Address varchar(255),\n" +
                "    City varchar(255) );";
        Mockito.doReturn(true).when(CSVHelperUtil).isExist(Mockito.anyString(), Mockito.anyString());
        assertThatThrownBy(() -> tableService.executeQuery(query)).hasMessage("Table already exists");
    }

    @SneakyThrows
    @Test
    void givenInvalidColumnInsertData_whenRunInsertQuery_thenThrowException() {

        //Given Invalid query with personId as string but not int
        String invalidQuery = "INSERT INTO Persons (PersonID) VALUES ('25482');";
        Mockito.doReturn(true).when(CSVHelperUtil).isExist(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(List.of( List.of("PersonID"), List.of("int"))).when(CSVHelperUtil).readFileToList(Mockito.anyString(), Mockito.anyString());

        // When
        ThrowableAssert.ThrowingCallable queryRun = () -> tableService.executeQuery(invalidQuery);

        // Then
        assertThatThrownBy(queryRun).hasMessageContaining("value : '25482' is not valid for column PersonID");
    }

    @SneakyThrows
    @Test
    void givenValidColumnInsertData_whenRunInsertQuery_thenSaveData() throws IOException {

        //Given valid query with personId as int
        String validQuery = "INSERT INTO Persons (PersonID) VALUES (25482);";
        Mockito.doReturn(true).when(CSVHelperUtil).isExist(Mockito.anyString(), Mockito.anyString());
        Mockito.doReturn(List.of( List.of("PersonID"), List.of("int"))).when(CSVHelperUtil).readFileToList(Mockito.anyString(), Mockito.anyString());
        ArgumentCaptor<List<String>> rowCap = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<String> tableCap = ArgumentCaptor.forClass(String.class);
        Mockito.doNothing().when(CSVHelperUtil).saveToCSV(rowCap.capture(), tableCap.capture(), Mockito.anyString());

        // When
        tableService.executeQuery(validQuery);

        // Then
        assertThat(tableCap.getValue()).isEqualToIgnoringWhitespace("Persons");
        assertThat(rowCap.getValue().get(0)).isEqualToIgnoringWhitespace("25482");
    }

    @SneakyThrows
    @Test
    void givenInsertDataWithInvalidTable_whenRunInsertQuery_thenThrowException() {

        //Given query with invalid table
        String validQuery = "INSERT INTO Persons (PersonID) VALUES ('25482');";
        Mockito.doReturn(false).when(CSVHelperUtil).isExist(Mockito.anyString(), Mockito.anyString());

        // When
        ThrowableAssert.ThrowingCallable queryRun = () -> tableService.executeQuery(validQuery);

        // Then
        assertThatThrownBy(queryRun).hasMessageContaining("Table Don't exist");
    }

}