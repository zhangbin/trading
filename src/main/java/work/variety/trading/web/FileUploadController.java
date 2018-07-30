package work.variety.trading.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import work.variety.trading.exception.ErrorFileException;
import work.variety.trading.exception.StorageFileNotFoundException;
import work.variety.trading.service.FileParseService;
import work.variety.trading.service.StorageService;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author zhangbin
 * @date 2018/7/25 10:44
 */
@Controller
@RequestMapping("/upload")
public class FileUploadController {
  @Autowired
  private StorageService storageService;
  @Autowired
  private FileParseService fileParseService;

  @GetMapping("")
  public String listUploadedFiles(Model model) throws IOException {

    return "uploadForm";
  }

  @PostMapping("")
  public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {

    try {
      String filename = storageService.store(file);

      fileParseService.parse(filename);
      redirectAttributes.addFlashAttribute("message",
        "成功上传并解析文件： " + file.getOriginalFilename() + "!");
    } catch (ErrorFileException e) {
      redirectAttributes.addFlashAttribute("message", e.getMessage());
    }
    return "redirect:/upload";
  }

  @ExceptionHandler(StorageFileNotFoundException.class)
  public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
    return ResponseEntity.notFound().build();
  }
}
