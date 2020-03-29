package com.xingren.excel;

import com.xingren.excel.entity.Book;
import com.xingren.excel.entity.BookNoError;
import com.xingren.excel.entity.Employee;
import com.xingren.excel.enums.ExcelType;
import com.xingren.excel.exception.ExcelException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author guang
 * @since 2020/2/10 9:46 上午
 */
public class ExcelReaderTest {

    private static String employeeFile_Xlsx;
    private static String bookFile_Xlsx;
    private static String bookEnumFile_Xlsx;
    private static String employeeFile_Xls;
    private static String employeeEmptyFile_Xls;
    private static String employeeFile_1w_Xlsx;
    private static String mult_sheet_Xlsx;

    @BeforeClass
    public static void before() throws UnsupportedEncodingException {
        ClassLoader classLoader = ExcelReaderTest.class.getClassLoader();
        employeeFile_Xls = URLDecoder.decode(classLoader.getResource("read/员工数据.xls").getPath(), "utf-8");
        bookFile_Xlsx = URLDecoder.decode(classLoader.getResource("read/图书数据.xlsx").getPath(), "utf-8");
        bookEnumFile_Xlsx = URLDecoder.decode(classLoader.getResource("read/图书数据-枚举值为空.xlsx").getPath(), "utf-8");
        employeeEmptyFile_Xls = URLDecoder.decode(classLoader.getResource("read/空员工数据.xls").getPath(), "utf-8");
        employeeFile_Xlsx = URLDecoder.decode(classLoader.getResource("read/员工数据.xlsx").getPath(), "utf-8");
        employeeFile_1w_Xlsx = URLDecoder.decode(classLoader.getResource("read/员工数据_1w.xlsx").getPath(), "utf-8");
        mult_sheet_Xlsx = URLDecoder.decode(classLoader.getResource("read/多sheet导入.xlsx").getPath(), "utf-8");
    }

    @Test
    public void test_read_empty() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(employeeEmptyFile_Xls);
        List<Employee> employees = ExcelReader
                .read(fileInputStream, ExcelType.XLS)
                .columnNameRowNum(1)
                .toPojo(Employee.class);
        Assert.assertNotNull(employees.size() == 0);
    }

    /**
     * 1w 行数据导入测试 -- 3s
     *
     * @throws FileNotFoundException
     */
    @Test
    public void test_import_1w_xls() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(employeeFile_1w_Xlsx);
        List<Employee> employees = ExcelReader
                .read(fileInputStream, ExcelType.XLSX)
                .columnNameRowNum(1)
                .toPojo(Employee.class);
        Assert.assertNotNull(employees);
    }

    @Test
    public void test_import_xls() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(employeeFile_Xls);
        List<Employee> employees = ExcelReader
                .read(fileInputStream, ExcelType.XLS)
                .columnNameRowNum(1)
                .toPojo(Employee.class);
        Assert.assertNotNull(employees);
    }

    /**
     * 起始行不合法
     *
     * @throws FileNotFoundException
     */
    @Test(expected = ExcelException.class)
    public void test_exception_columnNameRowNum_import_xls() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(employeeFile_Xls);
        List<Employee> employees = ExcelReader
                .read(fileInputStream, ExcelType.XLS)
                .columnNameRowNum(0)
                .toPojo(Employee.class);
        Assert.assertNotNull(employees);
    }

    @Test
    public void test_import_xlsx() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(employeeFile_Xlsx);
        List<Employee> employees = ExcelReader
                .read(fileInputStream, ExcelType.XLSX)
                .columnNameRowNum(1)
                .toPojo(Employee.class);
        Assert.assertNotNull(employees);
    }

    @Test
    public void test_import_requied() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(bookFile_Xlsx);
        List<Book> books = ExcelReader
                .read(fileInputStream)
                .toPojo(Book.class);

        Optional<Book> errorBook =
                books.stream().filter(book -> StringUtils.isNotBlank(book.getErrorInfo())).findFirst();
        Assert.assertTrue(errorBook.isPresent());
    }

    @Test
    public void test_import_enum_value_is_null_error_row() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(bookEnumFile_Xlsx);
        List<Book> books = ExcelReader
                .read(fileInputStream)
                .toPojo(Book.class);
        List<Book> bookList =
                books.stream().filter(book -> StringUtils.isNotBlank(book.getErrorInfo())).collect(Collectors.toList());
        Assert.assertTrue(bookList.size() == 1);
    }

    @Test(expected = ExcelException.class)
    public void test_import_enum_value_is_null() throws FileNotFoundException {
        InputStream fileInputStream = new FileInputStream(bookEnumFile_Xlsx);
        List<BookNoError> books = ExcelReader
                .read(fileInputStream)
                .toPojo(BookNoError.class);

    }

    @Test
    public void test_mult_sheet() throws FileNotFoundException{
        InputStream fis = new FileInputStream(mult_sheet_Xlsx);
        try(ExcelReader reader = ExcelReader.read(fis)) {
            List<Book> books = reader.toPojo(0, 0, Book.class);
            List<Book> empty = reader.toPojo(1, 0, Book.class);
            List<Employee> employees = reader.toPojo(2, 1, Employee.class);
            Assert.assertEquals(books.size(), 2);
            Assert.assertEquals(empty.size(), 0);
            Assert.assertEquals(employees.size(), 2);
            reader.toPojo(-1, 0, Book.class);
        } catch (Exception e) {
            Assert.assertTrue(e instanceof InvalidParameterException);
        }
    }
}
