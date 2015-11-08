package fr.adrienbrault.idea.symfony2plugin.stubs.indexes;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.indexing.*;
import com.intellij.util.io.DataExternalizer;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import com.jetbrains.php.lang.PhpFileType;
import fr.adrienbrault.idea.symfony2plugin.Symfony2ProjectComponent;
import fr.adrienbrault.idea.symfony2plugin.doctrine.DoctrineUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLFileType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DoctrineMetadataFileStubIndex extends FileBasedIndexExtension<String, String> {

    public static final ID<String, String> KEY = ID.create("fr.adrienbrault.idea.symfony2plugin.doctrine_metadata");
    private final KeyDescriptor<String> myKeyDescriptor = new EnumeratorStringDescriptor();

    private static int MAX_FILE_BYTE_SIZE = 1048576;

    private static class MyStringStringFileContentDataIndexer implements DataIndexer<String, String, FileContent> {
        @NotNull
        @Override
        public Map<String, String> map(@NotNull FileContent fileContent) {

            Map<String, String> map = new HashMap<String, String>();

            PsiFile psiFile = fileContent.getPsiFile();
            if(!Symfony2ProjectComponent.isEnabledForIndex(psiFile.getProject()) || !isValidForIndex(fileContent, psiFile)) {
                return map;
            }

            Collection<Pair<String, String>> classRepositoryPair = DoctrineUtil.getClassRepositoryPair(psiFile);
            if(classRepositoryPair == null || classRepositoryPair.size() == 0) {
                return map;
            }

            for (Pair<String, String> pair : classRepositoryPair) {
                String first = pair.getFirst();
                if(first == null || first.length() == 0) {
                    continue;
                }
                map.put(first, pair.getSecond());
            }

            return map;
        }
    }

    @NotNull
    @Override
    public ID<String, String> getName() {
        return KEY;
    }

    @NotNull
    @Override
    public DataIndexer<String, String, FileContent> getIndexer() {
        return new MyStringStringFileContentDataIndexer();
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return this.myKeyDescriptor;
    }

    @NotNull
    @Override
    public DataExternalizer<String> getValueExternalizer() {
        return ContainerParameterStubIndex.StringDataExternalizer.STRING_DATA_EXTERNALIZER;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return new FileBasedIndex.InputFilter() {
            @Override
            public boolean acceptInput(@NotNull VirtualFile file) {
                FileType fileType = file.getFileType();
                return fileType == XmlFileType.INSTANCE || fileType == YAMLFileType.YML || fileType == PhpFileType.INSTANCE;
            }
        };
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    public static boolean isValidForIndex(FileContent inputData, PsiFile psiFile) {

        String fileName = psiFile.getName();

        if(fileName.startsWith(".") || fileName.contains("Test")) {
            return false;
        }

        // @TODO: filter .orm.xml?
        String extension = inputData.getFile().getExtension();
        if(extension == null || !(extension.equalsIgnoreCase("xml") || extension.equalsIgnoreCase("yml")|| extension.equalsIgnoreCase("yaml") || extension.equalsIgnoreCase("php"))) {
            return false;
        }

        if(inputData.getFile().getLength() > MAX_FILE_BYTE_SIZE) {
            return false;
        }

        return true;
    }
}