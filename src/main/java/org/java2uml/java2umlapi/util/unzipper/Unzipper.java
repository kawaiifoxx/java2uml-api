package org.java2uml.java2umlapi.util.unzipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * Utility class to unzip files from a given folder to a given folder.
 * </p>
 *
 * @author kawaiifox
 */
public abstract class Unzipper {

  /**
   * unzip a given file from srcZipPath and generate unzipped files in
   * destDirPath.
   *
   * @param srcZipPath  {@link Path} of source zip file.
   * @param destDirPath {@link Path} of destination where output needs to be
   *     generated.
   * @return {@link File} directory containing unzipped files.
   * @throws IOException if unable to create directory.
   */
  public static File unzipDir(Path srcZipPath, Path destDirPath)
      throws IOException {
    File zipFile = new File(srcZipPath.toAbsolutePath().toString());
    File destDir = new File(destDirPath.toAbsolutePath().toString());
    ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
    ZipEntry zipEntry = zis.getNextEntry();

    while (zipEntry != null) {
      File newFile = newFile(destDir, zipEntry);

      if (zipEntry.isDirectory()) {
        if (!newFile.isDirectory() && !newFile.mkdirs()) {
          throw new IOException("Failed to create directory " + newFile);
        }
      } else {
        writeData(zis, newFile);
      }

      zipEntry = zis.getNextEntry();
    }

    zis.closeEntry();
    zis.close();
    return destDir;
  }

  /**
   * Writes the data from {@link ZipInputStream} in to the specified {@link
   * File} newFile until the end of stream is reached.
   *
   * @param zis     {@link ZipInputStream} from which the data will be read.
   * @param newFile {@link File} in which data will be written.
   * @throws IOException if unable to create directory.
   */
  private static void writeData(ZipInputStream zis, File newFile)
      throws IOException {
    byte[] buffer = new byte[1024];
    File parent = newFile.getParentFile();

    if (!parent.isDirectory() && !parent.mkdirs()) {
      throw new IOException("Failed to create directory " + parent);
    }

    FileOutputStream fos = new FileOutputStream(newFile);

    int len;
    while ((len = zis.read(buffer)) > 0) {
      fos.write(buffer, 0, len);
    }

    fos.close();
  }

  /**
   * Generates new file in given destination directory according to zipEntry.
   *
   * @param destDir  Directory where file needs to be created.
   * @param zipEntry ZipEntry for file being created.
   * @return Returns newly created file.
   * @throws IOException exception is thrown in case of zip slip.
   */
  private static File newFile(File destDir, ZipEntry zipEntry)
      throws IOException {
    File destFile = new File(destDir, zipEntry.getName());

    String destDirPath = destDir.getCanonicalPath();
    String destFilePath = destFile.getCanonicalPath();

    if (!destFilePath.startsWith(destDirPath + File.separator)) {
      throw new IOException("Entry is outside of the target dir: " +
                            zipEntry.getName());
    }

    return destFile;
  }
}
