package oracle.sql;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import oracle.jdbc.driver.OracleLog;

public class ConverterArchive {
    private String m_izipName;
    private FileOutputStream m_ifStream = null;
    private ZipOutputStream m_izStream = null;
    private InputStream m_riStream = null;
    private ZipFile m_rzipFile = null;
    private static final String TEMPFILE = "gsstemp.zip";
    private static final String _Copyright_2004_Oracle_All_Rights_Reserved_ = null;

    public static boolean TRACE = false;
    public static final boolean PRIVATE_TRACE = false;
    public static final String BUILD_DATE = "Wed_Jun_22_11:30:47_PDT_2005";

    public void openArchiveforInsert(String zipName) {
        this.m_izipName = zipName;
        try {
            this.m_ifStream = new FileOutputStream(this.m_izipName);
            this.m_izStream = new ZipOutputStream(this.m_ifStream);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
        }
    }

    public void closeArchiveforInsert() {
        try {
            this.m_izStream.close();
            this.m_ifStream.close();
        } catch (IOException ex) {
        }
    }

    public void insertObj(Object obj, String entryName) {
        ZipEntry zEntry = null;
        ObjectOutputStream objStream = null;

        zEntry = new ZipEntry(entryName);
        try {
            this.m_izStream.putNextEntry(zEntry);

            objStream = new ObjectOutputStream(this.m_izStream);

            objStream.writeObject(obj);
            objStream.close();
            this.m_izStream.closeEntry();
        } catch (IOException ex) {
        }
    }

    public void insertSingleObj(String zipName, Object obj, String entryName) throws IOException {
        FileInputStream fiStream = null;
        ZipInputStream ziStream = null;
        FileOutputStream foStream = null;
        ZipOutputStream zoStream = null;
        ZipEntry zEntry = null;

        ObjectInputStream oiStream = null;
        ObjectOutputStream ooStream = null;

        File targetZip = new File(zipName);

        if (targetZip.isFile()) {
            try {
                fiStream = new FileInputStream(zipName);
                ziStream = new ZipInputStream(fiStream);

                foStream = new FileOutputStream("gsstemp.zip");
                zoStream = new ZipOutputStream(foStream);

                int ab = ziStream.available();

                while (ziStream.available() != 0) {
                    zEntry = ziStream.getNextEntry();

                    if ((zEntry == null) || (zEntry.getName().equals(entryName))) {
                        continue;
                    }
                    zoStream.putNextEntry(zEntry);

                    oiStream = new ObjectInputStream(ziStream);
                    ooStream = new ObjectOutputStream(zoStream);
                    Object curobj = oiStream.readObject();

                    ooStream.writeObject(curobj);
                }

                zEntry = new ZipEntry(entryName);

                zoStream.putNextEntry(zEntry);

                ooStream = new ObjectOutputStream(zoStream);

                ooStream.writeObject(obj);
                ooStream.close();
                ziStream.close();
            } catch (FileNotFoundException ex) {
                throw new IOException(ex.getMessage());
            } catch (StreamCorruptedException ex) {
                throw new IOException(ex.getMessage());
            } catch (IOException ex) {
                throw ex;
            } catch (ClassNotFoundException ex) {
                throw new IOException(ex.getMessage());
            }

            File tempZip = new File("gsstemp.zip");

            targetZip.delete();
            try {
                if (!tempZip.renameTo(targetZip))
                    throw new IOException("can't write to target file " + zipName);
            } catch (SecurityException se) {
                throw new IOException(se.getMessage());
            } catch (NullPointerException ne) {
                throw new IOException(ne.getMessage());
            }

        } else {
            try {
                foStream = new FileOutputStream(zipName);
                zoStream = new ZipOutputStream(foStream);
                zEntry = new ZipEntry(entryName);

                zoStream.putNextEntry(zEntry);

                ooStream = new ObjectOutputStream(zoStream);

                ooStream.writeObject(obj);
                ooStream.close();
            } catch (FileNotFoundException ex) {
                throw new IOException(ex.getMessage());
            } catch (StreamCorruptedException ex) {
                throw new IOException(ex.getMessage());
            } catch (IOException ex) {
                throw ex;
            }
        }

        System.out.print(entryName + " has been successfully stored in ");
        System.out.println(zipName);
    }

    public void insertObjtoFile(String directory, String filename, Object obj) throws IOException {
        File dir = new File(directory);
        File file = new File(directory + filename);

        if (!dir.isDirectory()) {
            throw new IOException("directory " + directory + " doesn't exist");
        }

        if (file.exists()) {
            try {
                file.delete();
            } catch (SecurityException ex) {
                throw new IOException("file exist, can't overwrite file.");
            }
        }

        try {
            FileOutputStream foStream = new FileOutputStream(file);
            ObjectOutputStream ooStream = new ObjectOutputStream(foStream);

            ooStream.writeObject(obj);
            ooStream.close();
        } catch (FileNotFoundException ex) {
            throw new IOException("file can't be created.");
        }
        ObjectOutputStream ooStream;
        FileOutputStream foStream;
        System.out.print(filename + " has been successfully stored in ");
        System.out.println(directory);
    }

    public void openArchiveforRead() {
        try {
            this.m_rzipFile = new ZipFile(this.m_izipName);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public void closeArchiveforRead() {
        try {
            this.m_rzipFile.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    public Object readObj(String entryName) {
        URL objfile = getClass().getResource(entryName);

        if (objfile == null) {
            return null;
        }
        try {
            InputStream iStream = objfile.openStream();
            ObjectInputStream objStream = null;
            Object obj = null;

            objStream = new ObjectInputStream(iStream);
            obj = objStream.readObject();

            return obj;
        } catch (IOException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.conversionLogger.log(Level.SEVERE, "Orai18n version mismatch"
                        + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        } catch (ClassNotFoundException ex) {
            if ((TRACE) && (!OracleLog.recursiveTrace)) {
                OracleLog.recursiveTrace = true;
                OracleLog.conversionLogger.log(Level.SEVERE, "Orai18n version mismatch"
                        + ex.getMessage(), this);

                OracleLog.recursiveTrace = false;
            }

        }

        return null;
    }

    public Object readObj(String zipName, String entryName) {
        try {
            FileInputStream fiStream = new FileInputStream(zipName);
            ZipInputStream ziStream = new ZipInputStream(fiStream);
            ZipEntry zEntry = null;
            ObjectInputStream oiStream = null;
            Object obj = null;
            int ab = ziStream.available();

            while (ziStream.available() != 0) {
                zEntry = ziStream.getNextEntry();

                if ((zEntry == null) || (!zEntry.getName().equals(entryName))) {
                    continue;
                }
                oiStream = new ObjectInputStream(ziStream);
                obj = oiStream.readObject();
            }

            ziStream.close();

            return obj;
        } catch (IOException ex) {
        } catch (ClassNotFoundException ex) {
        }
        return null;
    }

    static {
        try {
            TRACE = OracleLog.registerClassNameAndGetCurrentTraceSetting(Class
                    .forName("oracle.sql.ConverterArchive"));
        } catch (Exception e) {
        }
    }
}

/*
 * Location: D:\oracle\product\10.2.0\client_1\jdbc\lib\ojdbc14_g.jar Qualified Name:
 * oracle.sql.ConverterArchive JD-Core Version: 0.6.0
 */