package org.joeffice.spreadsheet.csv;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.h2.tools.Csv;
import org.openide.util.Exceptions;

/**
 * Smart CSV reader is a CSV reader that is able to detect:
 * <ul><li>The character encoding of the file</li>
 * <li>The field delimiter</li>
 * <li>The escape character (quote)</li>
 * </ul>
 *
 * @author Anthony Goubard - Japplis
 */
public class SmartCsvReader {

    private char fieldSeparator;
    private Charset charset;
    private char escapeCharater;
    private String[] headers;

    public SmartCsvReader() {
    }

    protected void detect(File csvFile) {
        List<String> lines = detectCharset(csvFile.toPath());
        String header = lines.get(0);
        detectDelimiter(header);
        headers = getValues(header, true);
        detectEscapeCharacter(lines);
    }

    private List<String> detectCharset(Path csvPath) {
        List<String> lines = read(csvPath, Charset.defaultCharset());
        if (lines == null) {
            lines = read(csvPath, Charset.forName("UTF-8"));
        }
        if (lines == null) {
            lines = read(csvPath, Charset.forName("ISO-8859-1"));
        }
        return lines;
    }

    private List<String> read(Path path, Charset charset) {
        try {
            List<String> lines = Files.readAllLines(path, charset);
            this.charset = charset;
            return lines;
        } catch (IOException iex) {
            // Wrong charset probably
            return null;
        }
    }

    private void detectDelimiter(String header) {
        int tabCount = header.split("\t").length;
        int commaCount = header.split(",").length;
        int semiColomCount = header.split(";").length;
        if (tabCount > commaCount && tabCount > semiColomCount) {
            fieldSeparator = '\t';
        } else if (commaCount > tabCount && commaCount > semiColomCount) {
            fieldSeparator = ',';
        } else if (semiColomCount > tabCount && semiColomCount > commaCount) {
            fieldSeparator = ';';
        } else {
            fieldSeparator = '\t';
        }
    }

    public String[] getValues(String line, boolean removeQuotes) {
        // This won't work if a delimiter is in a quoted text
        String[] values;
        if (fieldSeparator == '\t') {
            values = line.split("\\t");
        } else {
            values = line.split("" + fieldSeparator);
        }
        if (removeQuotes && escapeCharater > 0) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (value.length() > 1 && value.startsWith("" + escapeCharater) && value.endsWith("" + escapeCharater)) {
                    values[i] = value.substring(1, value.length() - 1);
                }
            }
        }
        return values;
    }

    private void detectEscapeCharacter(List<String> lines) {
        int quoteCount = 0;
        int doubleQuoteCount = 0;
        for (String line : lines) {
            String[] values = getValues(line, false);
            for (String value : values) {
                if (value.startsWith("'") && value.endsWith("'")) quoteCount++;
                if (value.startsWith("\"") && value.endsWith("\"")) doubleQuoteCount++;
                if (quoteCount > 20 && doubleQuoteCount < quoteCount / 10) {
                    escapeCharater = '\'';
                    break;
                }
                if (doubleQuoteCount > 20 && quoteCount < doubleQuoteCount / 10) {
                    escapeCharater = '\"';
                    break;
                }
            }
            if (escapeCharater == 0 && quoteCount > doubleQuoteCount) {
                escapeCharater = '\'';
            } else if (escapeCharater == 0) {
                escapeCharater = '\"';
            }
        }
    }

    public Workbook read(File csvFile) throws IOException {
        detect(csvFile);

        Workbook csvWorkbook = new XSSFWorkbook();
        Sheet csvSheet = csvWorkbook.createSheet(csvFile.getName());

        Reader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), getCharset()));
        Csv csv = new Csv();
        csv.setEscapeCharacter(getEscapeCharater());
        csv.setFieldDelimiter(getEscapeCharater());
        csv.setFieldSeparatorRead(getFieldSeparator());
        csv.setFieldSeparatorWrite("" + getFieldSeparator());
        ResultSet rs = csv.read(csvReader, getHeaders());
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int rowIndex = 0; // First row contains the headers
            while (rs.next()) {
                Row dataRow = csvSheet.createRow(rowIndex);
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    Cell dataCell = dataRow.createCell(i);
                    dataCell.setCellValue(rs.getString(i + 1));
                }
                rowIndex++;
            }
            rs.close();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
        csvSheet.setDefaultColumnWidth(-1);
        return csvWorkbook;
    }

    public char getFieldSeparator() {
        return fieldSeparator;
    }

    public void setFieldSeparator(char fieldSeparator) {
        this.fieldSeparator = fieldSeparator;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public char getEscapeCharater() {
        return escapeCharater;
    }

    public void setEscapeCharater(char escapeCharater) {
        this.escapeCharater = escapeCharater;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
}