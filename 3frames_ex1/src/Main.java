import java.util.ArrayList;
import java.util.List;

// changes between versions
class Delta {
    private String changes;
    private FileVersion previousVersion;

    public Delta(String changes, FileVersion previousVersion) {
        this.changes = changes;
        this.previousVersion = previousVersion;
    }

    public String getChanges() {
        return changes;
    }

    public FileVersion getPreviousVersion() {
        return previousVersion;
    }
}

// version of the file
class FileVersion {
    private String content; // Content of the file version

    public FileVersion(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

// Manages versions and deltas of the file
class VersionManager {
    private FileVersion baseVersion;
    private List<Delta> deltas;

    public VersionManager(FileVersion baseVersion) {
        this.baseVersion = baseVersion;
        this.deltas = new ArrayList<>();
    }

    // Add a new delta to the list
    public void addDelta(Delta delta) {
        deltas.add(delta);
    }

    // Generate a specific version of the file
    public FileVersion generateVersion(int targetVersion) {
        FileVersion currentVersion = baseVersion;
        // Apply deltas sequentially until the target version is reached
        for (Delta delta : deltas) {
            currentVersion = applyDelta(currentVersion, delta);
            if (currentVersion == null) {
                return null; // Unable to generate version
            }
            if (currentVersion.getContent().equals(getVersionContent(targetVersion))) {
                return currentVersion;
            }
        }
        return null; // Target version not found
    }

    // Apply changes from a delta to a version
    private FileVersion applyDelta(FileVersion baseVersion, Delta delta) {
        String newContent = baseVersion.getContent() + "\n" + delta.getChanges();
        return new FileVersion(newContent);
    }

    // Get the content of a specific version
    private String getVersionContent(int versionNumber) {
        FileVersion currentVersion = baseVersion;
        // Apply deltas up to the specified version number
        for (int i = 0; i < versionNumber; i++) {
            if (i < deltas.size()) {
                currentVersion = applyDelta(currentVersion, deltas.get(i));
            } else {
                return null; // Version number exceeds available deltas
            }
        }
        return currentVersion.getContent();
    }
}

public class Main {
    public static void main(String[] args) {
        //base version
        FileVersion baseVersion = new FileVersion("Initial content of the file.");

        // version manager
        VersionManager versionManager = new VersionManager(baseVersion);

        // Generate subsequent versions and store deltas
        Delta delta1 = new Delta("Updated content: Added new line.", baseVersion);
        versionManager.addDelta(delta1);

        Delta delta2 = new Delta("Modified content: Changed some text.", baseVersion);
        versionManager.addDelta(delta2);

        // Generate a specific version
        int targetVersion = 2;
        FileVersion targetFileVersion = versionManager.generateVersion(targetVersion);
        if (targetFileVersion != null) {
            System.out.println("Content of version " + targetVersion + ": " + targetFileVersion.getContent());
        } else {
            System.out.println("Unable to generate version " + targetVersion);
        }
    }
}
