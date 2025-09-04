package com.redplutoanalytics.callpluto.service;
 
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
 
import com.redplutoanalytics.callpluto.dto.AudioFileDTO;
import com.redplutoanalytics.callpluto.dto.DashboardFilterDTO;
import com.redplutoanalytics.callpluto.repository.AudioLibraryRepository;
 
import jakarta.transaction.Transactional;
 
@Service
public class AudioLibraryService {
 
    @Autowired
    private AudioLibraryRepository audioLibraryRepository;
 
    public List<AudioFileDTO> getFilteredAudioFiles(DashboardFilterDTO filterDTO) {
         
        Map<String, Object> filters = filterDTO.toAudioLibraryMap();
 
        List<Map<String, Object>> audioWithScores = audioLibraryRepository.findFilteredAudioFiles(filters);
 
      
        Set<String> seenRecordingNames = new HashSet<>();
 
        return audioWithScores.stream()
            .map(row -> {   
                AudioFileDTO dto = new AudioFileDTO();
 
                dto.setId(row.get("id") != null ? ((Number) row.get("id")).longValue() : null);
                dto.setRecordingName((String) row.get("recordingName"));
                dto.setFileName((String) row.get("fileName"));
                dto.setTranscript((String) row.get("transcript"));
                dto.setTranslation((String) row.get("translation"));
 
                Object durationObj = row.get("duration");
                dto.setDuration(durationObj != null ? String.valueOf(durationObj) : null);
 
              
                Object createdDateObj = row.get("created_date");
                if (createdDateObj instanceof String) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        dto.setCreatedDate(LocalDateTime.parse((String) createdDateObj, formatter));
                    } catch (DateTimeParseException e) {
                        dto.setCreatedDate(null);
                    }
                }
 
             
                Object uploadDateObj = row.get("uploadDate");
                if (uploadDateObj instanceof String) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        dto.setUploadDate(LocalDateTime.parse((String) uploadDateObj, formatter));
                    } catch (DateTimeParseException e) {
                        dto.setUploadDate(null);
                    }
                }
 
                dto.setRmName((String) row.get("rmName"));
 
                Object csatScoreObj = row.get("csat_score");
                if (csatScoreObj instanceof Number) {
                    dto.setCsatScore(((Number) csatScoreObj).intValue());
                } else if (csatScoreObj instanceof String) {
                    try {
                        dto.setCsatScore(Integer.parseInt((String) csatScoreObj));
                    } catch (NumberFormatException e) {
                        dto.setCsatScore(null);
                    }
                } else {
                    dto.setCsatScore(null);
                }
 
                dto.setPositiveWords((String) row.get("positive_words"));
                dto.setNegativeWords((String) row.get("negative_words"));
                dto.setCallType((String) row.get("call_type"));
                dto.setSummary((String) row.get("summary"));
                dto.setActionItem((String) row.get("action_item"));
                dto.setStatus((String) row.get("status"));
 
                return dto;
            })
            .filter(dto -> seenRecordingNames.add(dto.getRecordingName())) // remove duplicates
            .collect(Collectors.toList());
    }
    
    
    @Transactional
    public void excelUpload(MultipartFile file, String type) throws Exception {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {
 
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
 
            for (Row row : sheet) {
                if (firstRow) { firstRow = false; continue; } // skip header
 
                if ("trading".equalsIgnoreCase(type)) {
                    Map<String, Object> map = parseTradingRow(row);
                    audioLibraryRepository.insertTradingOrder(map);
                } else if ("recording".equalsIgnoreCase(type)) {
                    Map<String, Object> map = parseRecordingRow(row);
                    audioLibraryRepository.insertRecordingDetails(map);
                } else {
                    throw new IllegalArgumentException("Unknown type: " + type);
                }
            }
        }
    }
 
    private Map<String, Object> parseTradingRow(Row row) {
        Map<String,Object> r = new HashMap<>();
        r.put("trading_order_details_id", getCellValueAsString(row.getCell(0)));
        r.put("client_id", getCellValueAsString(row.getCell(1)));
        r.put("rm_id", getCellValueAsString(row.getCell(2)));
        r.put("instrument_id", getCellValueAsString(row.getCell(3)));
        r.put("product_details_id", getCellValueAsString(row.getCell(4)));
        r.put("quantity", getCellValueAsString(row.getCell(5)));
        r.put("price", getCellValueAsString(row.getCell(6)));
        r.put("transaction_type", getCellValueAsString(row.getCell(7)));
        r.put("stop_loss", getCellValueAsString(row.getCell(8)));
        r.put("target_price", getCellValueAsString(row.getCell(9)));
        r.put("order_status", getCellValueAsString(row.getCell(10)));
        r.put("created_date", getCellValueAsString(row.getCell(11)));
        r.put("order_execution_time", getCellValueAsString(row.getCell(12)));
        return r;
    }
 
    private Map<String, Object> parseRecordingRow(Row row) {
        Map<String,Object> r = new HashMap<>();
        r.put("recording_details_id", getCellValueAsString(row.getCell(0)));
        r.put("recording_id", getCellValueAsString(row.getCell(1)));
        r.put("recording_name", getCellValueAsString(row.getCell(2)));
        r.put("client_id", getCellValueAsString(row.getCell(3)));
        r.put("rm_id", getCellValueAsString(row.getCell(4)));
        r.put("trading_order_details_id", getCellValueAsString(row.getCell(5)));
        r.put("recording_flag", getCellValueAsString(row.getCell(6)));
        r.put("call_type", getCellValueAsString(row.getCell(7)));
        r.put("start_time", getCellValueAsString(row.getCell(8)));
        r.put("end_time", getCellValueAsString(row.getCell(9)));
        r.put("created_date", getCellValueAsString(row.getCell(10)));
        return r;
    }
 
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime dt = cell.getLocalDateTimeCellValue();
                    return dt.toString();
                } else {
                    double val = cell.getNumericCellValue();
                    return (val == (long) val) ? String.valueOf((long) val) : String.valueOf(val);
                }
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            case FORMULA: return cell.getCellFormula();
            case BLANK: return "";
            default: return cell.toString();
        }
    }
}


