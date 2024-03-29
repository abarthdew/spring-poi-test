package com.tistory.pentode;

import com.tistory.pentode.service.BoardService;
import com.tistory.pentode.vo.BoardVO;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

  @Resource(name = "boardService")
  private BoardService boardService;

  private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

  /**
   * Simply selects the home view to render by returning its name.
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public String home(Locale locale, Model model) {
    logger.info("Welcome home! The client locale is {}.", locale);

    Date date = new Date();
    DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

    String formattedDate = dateFormat.format(date);

    model.addAttribute("serverTime", formattedDate);

    return "home";
  }

  /**
   * 게시판 목록
   *
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/boardList.do")
  public String boardList(Model model) throws Exception {

    List<BoardVO> list = boardService.selectBoardList();

    logger.info(list.toString());

    model.addAttribute("list", list);

    return "boardList";
  }

  /**
   * 게시판 등록폼
   *
   * @param boardVO
   * @param model
   * @return
   * @throws Exception
   */
  @RequestMapping(value = "/boardRegisterForm.do")
  public String boardRegisterForm(@ModelAttribute("boardVO") BoardVO boardVO, Model model) throws Exception {

    return "boardRegisterForm";
  }

  /**
   * 글을 등록합니다.
   *
   * @return
   */
  @RequestMapping(value = "/boardInsert.do")
  public String boardInsert(@ModelAttribute("boardVO") BoardVO boardVO, Model model) {

    boardService.insertBoard(boardVO);

    return "redirect:/boardList.do";
  }

  /**
   * 엑셀 다운로드
   */
  @RequestMapping(value = "/excelDown.do")
  public void excelDown(HttpServletResponse response) throws Exception {

    // 게시판 목록조회
    List<BoardVO> list = boardService.selectBoardList();

    // 워크북 생성
    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet("게시판");
    Row row = null;
    Cell cell = null;
    int rowNo = 0;

    // 테이블 헤더용 스타일
    CellStyle headStyle = wb.createCellStyle();
    // 가는 경계선을 가집니다.
    headStyle.setBorderTop(BorderStyle.THIN);
    headStyle.setBorderBottom(BorderStyle.THIN);
    headStyle.setBorderLeft(BorderStyle.THIN);
    headStyle.setBorderRight(BorderStyle.THIN);
    // 배경색은 노란색입니다.
    headStyle.setFillForegroundColor(HSSFColorPredefined.YELLOW.getIndex());
    headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    // 데이터는 가운데 정렬합니다.
    headStyle.setAlignment(HorizontalAlignment.CENTER);

    // 데이터용 경계 스타일
    CellStyle bodyStyle = wb.createCellStyle();
    bodyStyle.setBorderTop(BorderStyle.THIN);
    bodyStyle.setBorderBottom(BorderStyle.THIN);
    bodyStyle.setBorderLeft(BorderStyle.THIN);
    bodyStyle.setBorderRight(BorderStyle.THIN);

    // 헤더 생성
    row = sheet.createRow(rowNo++);
    cell = row.createCell(0);
    cell.setCellStyle(headStyle);
    cell.setCellValue("번호");
    cell = row.createCell(1);
    cell.setCellStyle(headStyle);
    cell.setCellValue("이름");
    cell = row.createCell(2);
    cell.setCellStyle(headStyle);
    cell.setCellValue("제목");

    // 데이터 부분 생성
    for (BoardVO vo : list) {
      row = sheet.createRow(rowNo++);
      cell = row.createCell(0);
      cell.setCellStyle(bodyStyle);
      cell.setCellValue(vo.getNum());
      cell = row.createCell(1);
      cell.setCellStyle(bodyStyle);
      cell.setCellValue(vo.getName());
      cell = row.createCell(2);
      cell.setCellStyle(bodyStyle);
      cell.setCellValue(vo.getTitle());
    }

    // 엑셀 출력
    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Content-Disposition", "attachment;filename=test.xls");

    wb.write(response.getOutputStream());
    wb.close();
  }
}
