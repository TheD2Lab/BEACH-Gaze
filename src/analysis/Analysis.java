package analysis;
import java.io.File;

public class Analysis {
    /*
     * Accepts parameters object and initilizes the analysis
     */

    private Parameters params;
    
    public Analysis(Parameters params) {
        this.params = params;
    }

    public void Start() {
        String[] inputFiles = params.getInputFiles();
        for (int i = 0; i < inputFiles.length; i++) {
            String fileName = inputFiles[i];
            File f = new File(fileName);
            
        }
    }

    public void ReadFile(File gaze) {

    }
}
