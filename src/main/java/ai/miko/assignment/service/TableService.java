package ai.miko.assignment.service;


import ai.miko.assignment.Exception.InvalidSqlStatementException;
import ai.miko.assignment.Exception.MiKoException;
import ai.miko.assignment.Exception.TableAlreadyExistsException;
import ai.miko.assignment.Exception.TableDoesntExistException;
import ai.miko.assignment.enums.Status;
import ai.miko.assignment.model.QueryStatus;
import ai.miko.assignment.repository.RedisRepository;
import ai.miko.assignment.utils.CSVHelperUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColDataType;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Limit;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableService {
    public static final String DEFAULT_DB = "default_db";

    private final CSVHelperUtil CSVHelperUtil;

    private final RedisRepository repository;

    public List<List<String>> select(Select st) {
        PlainSelect select = (PlainSelect) st.getSelectBody();

        // Limit offset
        Limit limit = select.getLimit();

        // TODO:// aggregations and partioning

        if(limit == null || limit.isLimitAll())
            return CSVHelperUtil.readFileToList(select.getFromItem().toString(), DEFAULT_DB);
        return CSVHelperUtil.readFileToList(select.getIntoTables().get(0).getName(), DEFAULT_DB).stream().limit(Long.parseLong(limit.getRowCount().toString())).toList();
    }

    public void insertData(Insert st) throws IOException {
        // Validate Table Exists
        if (!CSVHelperUtil.isExist(st.getTable().getName(), DEFAULT_DB))
            throw new TableDoesntExistException("Table Don't exist",st.toString());

        // Read CSV mdt
        List<List<String>> metaValidate = CSVHelperUtil.readFileToList(st.getTable().getName() + "_mtd", DEFAULT_DB);

        List<String> columns = st.getColumns().stream().map(Column::getColumnName).toList();
        List<String> values = List.of(st.getItemsList().toString().substring(1, st.getItemsList().toString().length() - 1).split(","));

        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < metaValidate.get(0).size(); i++) {
            map.put(metaValidate.get(0).get(i), metaValidate.get(1).get(i));
        }

        //Validate data and save to file
        if (validateColumns(map, columns, values, st.toString())) {
            CSVHelperUtil.saveToCSV(values, st.getTable().getName(), DEFAULT_DB);
        }

    }

    private boolean validateColumns(Map<String, String> map, List<String> columns, List<String> values, String query) {
        for (int i = 0; i < columns.size(); i++) {
            if (!map.containsKey(columns.get(i))) {
                throw new InvalidSqlStatementException("Column " + columns.get(i) + " does not exist in table", query);
            }
            String columnType = map.get(columns.get(i));
            String value = values.get(i);

            if (columnType.equals("int")) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    throw new InvalidSqlStatementException("value : " + value + " is not valid for column " + columns.get(i), query);
                }
            }
        }
        return true;
    }

    @SneakyThrows
    public Object executeQuery(String query) {

        Statement st;
        try {
            st = CCJSqlParserUtil.parse(query);
        } catch (JSQLParserException e) {
            throw new InvalidSqlStatementException("Invalid Sql Statement, returning with error: " + e.getMessage(), query);
        }

        if (st instanceof CreateTable) {
            createMeta((CreateTable) st);
        } else if (st instanceof Insert) {
            insertData((Insert) st);
        } else if(st instanceof Select) {
            return select((Select) st);
        }

        repository.save(QueryStatus.builder().status(Status.SUCCESS).query(query).build());
        return null;
    }

    public List<QueryStatus> getHistory() {
        return repository.findAll();
    }

    private void createMeta(CreateTable st) {
        // Check if exists
        if (CSVHelperUtil.isExist(st.getTable().getName(), DEFAULT_DB))
            throw new TableAlreadyExistsException("Table already exists", st.toString());

        // Create meta file
        CSVHelperUtil.saveToCSV(st.getColumnDefinitions().stream().map(ColumnDefinition::getColumnName)
                .collect(Collectors.toList()), st.getTable().getName() + "_mtd", DEFAULT_DB);
        CSVHelperUtil.saveToCSV(st.getColumnDefinitions().stream().map(ColumnDefinition::getColDataType)
                .map(ColDataType::toString).collect(Collectors.toList()), st.getTable().getName() + "_mtd", DEFAULT_DB);

        // Create data file
        CSVHelperUtil.saveToCSV(st.getColumnDefinitions().stream().map(ColumnDefinition::getColumnName).collect(Collectors.toList()), st.getTable().getName(), DEFAULT_DB);
    }

}
