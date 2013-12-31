import java.util.List;

File     licFile;
String[] licText;

void setup() {
  // Load the licence text and append inital and terminating multiline comment
  licFile = new File(sketchPath("../../licence.txt"));
  licText = loadStrings(licFile.getAbsolutePath());
  licText = splice(licText, "/*", 0);
  licText = append(licText, "*/");

  List<File> filesToUpdate = selectFiles("[\\w]+\\.java");
  for( File sourceFile : filesToUpdate ) {
    println("updating: " + sourceFile);
    updateFile(sourceFile);
  }
  
  exit();
}

// ---------------------------------------------------------------------------------- //
List<File> selectFiles( String types ) {
  List<File> out    = new ArrayList<File>();
  
  selectFilesImpl( out, new File( sketchPath("../../src.lang") ),      types );
  selectFilesImpl( out, new File( sketchPath("../../src.math") ),      types );
  selectFilesImpl( out, new File( sketchPath("../../src.mesh") ),      types );
  selectFilesImpl( out, new File( sketchPath("../../src.net") ),       types );
  selectFilesImpl( out, new File( sketchPath("../../src.serialize") ), types );
  selectFilesImpl( out, new File( sketchPath("../../src.test") ),      types );
  selectFilesImpl( out, new File( sketchPath("../../src.util") ),      types );
  
  return out;
}

// ---------------------------------------------------------------------------------- //
// Recurses on the folder provided searching for files matching the "types" string.
// See selectFiles for more information.
void selectFilesImpl( List<File> out, File folder, String types ) {
  if( !folder.isDirectory() ) return;
    
  for( File file : folder.listFiles() ) {
    selectFilesImpl( out, file, types );
    
    if( !file.getName().matches( types ) )
      continue;
      
    out.add( file );
  }
}

void updateFile(File sourceFile) {
  String[] sourceText = loadStrings(sourceFile.getAbsolutePath());
  
  int startComment = -1;
  int endComment   = -1;
  int index        =  0;
  for( String line : sourceText ) {
    if( line.trim().equals("") ) {
      ++index;
      continue;
    }
    
    if( line.contains("/*") ) {
      startComment = index;
    } else if( startComment != -1 && line.contains("*/") ) {
      endComment = index;
      break;
    } else if( startComment == -1 ) {
      // Didn't find a header comment, break for safety
      break;
    }
    ++index;
  }
  
  // If we found a licence comment
  if( startComment != -1 && endComment != -1 ) {
    // Remove the existing licence
    sourceText = subset(sourceText, endComment+1);
  }
  
  // Append the new licence comment
  sourceText = splice(sourceText, licText, 0);
  saveStrings(sourceFile.getAbsolutePath(), sourceText);
}

