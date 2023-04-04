package app;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import model.*;

import static app.Constant.ERROR_STATE.*;
import static app.Constant.ERROR_MSG;
import static model.Attribute.ATTRIBUTE;
import static model.CharacterMap.CHAR_MAP;
import static model.RowMap.ROW_MAP;
import static model.TransitionTable.TRANSITION;

class VCLexer {

  /** the input device */
  private Reader reader;

  /** the current state of the DFA */
  private int state;

  /** the current lexical state */
  private int lexicalState = Constant.INITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char buffer[] = new char[Constant.BUFFER_SIZE];

  /** the textposition at the last accepting state */
  private int markedPos;

  /** the current text position in the buffer */
  private int currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int numOfLine;

  /** the number of characters up to the start of the matched text */
  private int numOfChar;

  /**
   * the number of characters from the last newline up to the start of the
   * matched text
   */
  private int column;

  /**
   * atBOL == true iff the scanner is currently at the beginning of a line
   */
  private boolean atBOL = true;

  /** atEOF == true iff the scanner is at the EOF */
  private boolean atEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean isEOF;
  
  /** 
   * The number of occupied positions in zzBuffer beyond zzEndRead.
   * When a lead/high surrogate has been read from the input stream
   * into the final zzBuffer position, this will have a value of 1;
   * otherwise, it will have a value of 0.
   */
  private int finalHighSurrogate = 0;

  private List<String> tokens = new ArrayList<>();

  /* user code: */
	public void addToken(String token, String typeToken){
	  String key = "Token: " + token.trim() + " " + "Type: " + typeToken;
	  if (typeToken.equals("Identifier")){
	    if (!tokens.contains(key)) {
          tokens.add(key);
        }
      }else {
        tokens.add(key);
      }

	}
	public void printTokens() {
      for (String s : tokens) System.out.println(s);
    }

	public void printError(String error){
		System.out.println("Error: " + error);
	}


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  VCLexer(java.io.Reader in) {
    this.reader = in;
  }

  /**
   * Refills the input buffer.
   *
   * @return      <code>false</code>, iff there was new input.
   * 
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean refill() throws java.io.IOException {

    /* first: make room (if you can) */
    if (startRead > 0) {
      endRead += finalHighSurrogate;
      finalHighSurrogate = 0;
      System.arraycopy(buffer, startRead,
              buffer, 0,
                       endRead - startRead);

      /* translate stored positions */
      endRead -= startRead;
      currentPos -= startRead;
      markedPos -= startRead;
      startRead = 0;
    }

    /* is the buffer big enough? */
    if (currentPos >= buffer.length - finalHighSurrogate) {
      /* if not: blow it up */
      char newBuffer[] = new char[buffer.length*2];
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
      buffer = newBuffer;
      endRead += finalHighSurrogate;
      finalHighSurrogate = 0;
    }

    /* fill the buffer with new input */
    int requested = buffer.length - endRead;
    int numRead = reader.read(buffer, endRead, requested);

    /* not supposed to occur according to specification of java.io.Reader */
    if (numRead == 0) {
      throw new java.io.IOException("Reader returned 0 characters. See JFlex examples for workaround.");
    }
    if (numRead > 0) {
      endRead += numRead;
      /* If numRead == requested, we might have requested to few chars to
         encode a full Unicode character. We assume that a Reader would
         otherwise never return half characters. */
      if (numRead == requested) {
        if (Character.isHighSurrogate(buffer[endRead - 1])) {
          --endRead;
          finalHighSurrogate = 1;
        }
      }
      /* potentially more input available */
      return false;
    }

    /* numRead < 0 ==> end of stream */
    return true;
  }


  /**
   * Closes the input stream.
   */
  public final void close() throws java.io.IOException {
    atEOF = true;            /* indicate end of file */
    endRead = startRead;     /* invalidate buffer    */

    if (reader != null)
      reader.close();
  }


  /**
   * Resets the scanner to read from a new input stream.
   * Does not close the old reader.
   *
   * All internal variables are reset, the old input stream
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>ZZ_INITIAL</tt>.
   *
   * Internal scan buffer is resized down to its initial length, if it has grown.
   *
   * @param reader   the new input stream
   */
  public final void reset(java.io.Reader reader) {
    reader = reader;
    atBOL = true;
    atEOF = false;
    isEOF = false;
    endRead = startRead = 0;
    currentPos = markedPos = 0;
    finalHighSurrogate = 0;
    numOfLine = numOfChar = column = 0;
    lexicalState = Constant.INITIAL;
    if (buffer.length > Constant.BUFFER_SIZE)
      buffer = new char[Constant.BUFFER_SIZE];
  }


  /**
   * Returns the current lexical state.
   */
  public final int getLexicalState() {
    return lexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void begin(int newState) {
    lexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final String text() {
    return new String(buffer, startRead, markedPos - startRead);
  }


  /**
   * Returns the character at position <tt>pos</tt> from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char getCharAt(int pos) {
    return buffer[startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int getMatchedTextLength() {
    return markedPos - startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void scanError(int errorCode) {
    String message;
    try {
      message = ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ERROR_MSG[UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void pushBack(int number)  {
    if ( number > getMatchedTextLength() )
      scanError(PUSHBACK_2BIG);

    markedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public int lexical() throws java.io.IOException {
    int input;
    int action;

    // cached fields:
    int curPos;
    int markedPos;
    int endRead = this.endRead;
    char [] buffer = this.buffer;
    char [] charMap = CHAR_MAP;

    int [] transition = TRANSITION;
    int [] rowMap = ROW_MAP;
    int [] attribute = ATTRIBUTE;

    while (true) {
      markedPos = this.markedPos;

      boolean flag = false;
      int character;
      int characterCount;
      for (curPos = startRead;
           curPos < markedPos ;
           curPos += characterCount ) {
        character = Character.codePointAt(buffer, curPos, markedPos);
        characterCount = Character.charCount(character);
        switch (character) {
        case '\u000B':  // fall through
        case '\u000C':  // fall through
        case '\u0085':  // fall through
        case '\u2028':  // fall through
        case '\u2029':
          numOfLine++;
          column = 0;
          flag = false;
          break;
        case '\r':
          numOfLine++;
          column = 0;
          flag = true;
          break;
        case '\n':
          if (flag)
            flag = false;
          else {
            numOfLine++;
            column = 0;
          }
          break;
        default:
          flag = false;
          column += characterCount;
        }
      }

      if (flag) {
        // peek one character ahead if it is \n (if we have counted one line too much)
        boolean zzPeek;
        if (markedPos < endRead)
          zzPeek = buffer[markedPos] == '\n';
        else if (atEOF)
          zzPeek = false;
        else {
          boolean eof = refill();
          endRead = this.endRead;
          markedPos = this.markedPos;
          buffer = this.buffer;
          if (eof) 
            zzPeek = false;
          else 
            zzPeek = buffer[markedPos] == '\n';
        }
        if (zzPeek) numOfLine--;
      }
      action = -1;

      curPos = currentPos = startRead = markedPos;
  
      state = Constant.LEX_STATE[lexicalState];

      // set up action for empty match case:
      int attributes = attribute[state];
      if ( (attributes & 1) == 1 ) {
        action = state;
      }


      forAction: {
        while (true) {
          if (curPos < endRead) {
            input = Character.codePointAt(buffer, curPos, endRead);
            curPos += Character.charCount(input);
          }
          else if (atEOF) {
            input = Constant.EOF;
            break forAction;
          }
          else {
            // store back cached positions
            currentPos = curPos;
            this.markedPos = markedPos;
            boolean eof = refill();
            // get translated positions and possibly new buffer
            curPos  = currentPos;
            markedPos   = this.markedPos;
            buffer      = this.buffer;
            endRead     = this.endRead;
            if (eof) {
              input = Constant.EOF;
              break forAction;
            }
            else {
              input = Character.codePointAt(buffer, curPos, endRead);
              curPos += Character.charCount(input);
            }
          }
          int next = transition[ rowMap[state] + charMap[input] ];
          if (next == -1) break forAction;
          state = next;

          attributes = attribute[state];
          if ( (attributes & 1) == 1 ) {
            action = state;
            markedPos = curPos;
            if ( (attributes & 8) == 8 ) break forAction;
          }

        }
      }

      // store back cached position
      this.markedPos = markedPos;

      if (input == Constant.EOF && startRead == currentPos) {
        atEOF = true;
        return Constant.EOF;
      }
      else {
        switch (action < 0 ? action : Action.ACTION[action]) {
          case 1: 
            { addToken(text(), "Illegal char");
            } 
            // fall through
          case 19: break;
          case 2: 
            { /* Ignore whitespace */
            } 
            // fall through
          case 20: break;
          case 3: 
            { addToken(text(), "Arithmetic Operator");
            } 
            // fall through
          case 21: break;
          case 4: 
            { addToken(text(), "Identifier");
            } 
            // fall through
          case 22: break;
          case 5: 
            { addToken(text(), "Integer Literal");
            } 
            // fall through
          case 23: break;
          case 6: 
            { addToken(text(), "Relational Operator");
            } 
            // fall through
          case 24: break;
          case 7: 
            { addToken(text(), "Assignment Operator");
            } 
            // fall through
          case 25: break;
          case 8: 
            { addToken(text(), "Logical Operator");
            } 
            // fall through
          case 26: break;
          case 9: 
            { addToken(text(), "Separator");
            } 
            // fall through
          case 27: break;
          case 10: 
            { printError("String unterminated");
            } 
            // fall through
          case 28: break;
          case 11: 
            { System.out.print(text());
            } 
            // fall through
          case 29: break;
          case 12: 
            { /* Ignore comment */
            } 
            // fall through
          case 30: break;
          case 13: 
            { addToken(text(), "Floating-point Literal");
            } 
            // fall through
          case 31: break;
          case 14: 
            { addToken(text(), "Keyword");
            } 
            // fall through
          case 32: break;
          case 15: 
            { addToken(text(), "Equality Operator");
            } 
            // fall through
          case 33: break;
          case 16: 
            { addToken(text(), "String Literal");
            } 
            // fall through
          case 34: break;
          case 17: 
            { printError("Comment unterminated");
            } 
            // fall through
          case 35: break;
          case 18: 
            { addToken(text(), "Boolean Literal");
            } 
            // fall through
          case 36: break;
          default:
            scanError(NO_MATCH);
        }
      }
    }
  }

  /**
   * Runs the scanner on input files.
   *
   * This is a standalone scanner, it will print any unmatched
   * text to System.out unchanged.
   *
   * @param argv   the command line, contains the filenames to run
   *               the scanner on.
   */
  public static void main(String argv[]) {
    if (argv.length == 0) {
      System.out.println("No input file");
    }
    else {
      int firstFilePos = 0;
      String encodingName = "UTF-8";
      if (argv[0].equals("--encoding")) {
        firstFilePos = 2;
        encodingName = argv[1];
        try {
          java.nio.charset.Charset.forName(encodingName); // Side-effect: is encodingName valid? 
        } catch (Exception e) {
          System.out.println("Invalid encoding '" + encodingName + "'");
          return;
        }
      }
      for (int i = firstFilePos; i < argv.length; i++) {
        VCLexer scanner = null;
        try {
          java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
          java.io.Reader reader = new java.io.InputStreamReader(stream, encodingName);
          scanner = new VCLexer(reader);
          while ( !scanner.atEOF) {
            scanner.lexical();
          }
        }
        catch (java.io.FileNotFoundException e) {
          System.out.println("File not found : \""+argv[i]+"\"");
        }
        catch (java.io.IOException e) {
          System.out.println("IO error scanning file \""+argv[i]+"\"");
          System.out.println(e);
        }
        catch (Exception e) {
          System.out.println("Unexpected exception:");
          e.printStackTrace();
        }
        scanner.printTokens();
      }
    }
  }
}
