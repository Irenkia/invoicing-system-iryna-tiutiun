package pl.futurecollars.invoicing.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class FilesService {

  public void appendLineToFile(Path path, String line) {
    try {
      Files.write(path, (line + System.lineSeparator()).getBytes(), StandardOpenOption.APPEND);
    } catch (IOException e) {
      throw new RuntimeException("Failed to append line to file", e);
    }
  }

  public void writeToFile(Path path, String line) {
    try {
      Files.write(path, line.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to write to file", e);
    }
  }

  public void writeLinesToFile(Path path, List<String> lines) {
    try {
      Files.write(path, lines, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new RuntimeException("Failed to all lines to file", e);
    }
  }

  public List<String> readAllLines(Path path) {
    try {
      return Files.readAllLines(path);
    } catch (IOException e) {
      throw new RuntimeException("Failed read all lines from file", e);
    }
  }
}
