package com.hhoss.i18n;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;

public class I18nException extends Exception {

	   /** Field originalException */
	   protected Exception originalException = null;

	   /** Field msgID */
	   protected String msgID;

	   /**
	    * Constructor I18nException
	    *
	    */
	   public I18nException() {

	      super("Missing message string");

	      this.msgID = null;
	      this.originalException = null;
	   }

	   /**
	    * Constructor I18nException
	    *
	    * @param _msgID
	    */
	   public I18nException(String _msgID) {

	      super(I18n.getExceptionMessage(_msgID));

	      this.msgID = _msgID;
	      this.originalException = null;
	   }

	   /**
	    * Constructor I18nException
	    *
	    * @param _msgID
	    * @param exArgs
	    */
	   public I18nException(String _msgID, Object exArgs[]) {

	      super(MessageFormat.format(I18n.getExceptionMessage(_msgID), exArgs));

	      this.msgID = _msgID;
	      this.originalException = null;
	   }

	   /**
	    * Constructor I18nException
	    *
	    * @param _originalException
	    */
	   public I18nException(Exception _originalException) {

	      super("Missing message ID to locate message string in resource bundle \""
	            + I18n.exceptionMessagesResourceBundleBase
	            + "\". Original Exception was a "
	            + _originalException.getClass().getName() + " and message "
	            + _originalException.getMessage());

	      this.originalException = _originalException;
	   }

	   /**
	    * Constructor I18nException
	    *
	    * @param _msgID
	    * @param _originalException
	    */
	   public I18nException(String _msgID, Exception _originalException) {

	      super(I18n.getExceptionMessage(_msgID, _originalException));

	      this.msgID = _msgID;
	      this.originalException = _originalException;
	   }

	   /**
	    * Constructor I18nException
	    *
	    * @param _msgID
	    * @param exArgs
	    * @param _originalException
	    */
	   public I18nException(String _msgID, Object exArgs[],
	                               Exception _originalException) {

	      super(MessageFormat.format(I18n.getExceptionMessage(_msgID), exArgs));

	      this.msgID = _msgID;
	      this.originalException = _originalException;
	   }

	   /**
	    * Method getMsgID
	    *
	    * @return the messageId
	    */
	   public String getMsgID() {

	      if (msgID == null) {
	         return "Missing message ID";
	      }
	      return msgID;
	   }

	   /** @inheritDoc */
	   public String toString() {

	      String s = this.getClass().getName();
	      String message = super.getLocalizedMessage();

	      if (message != null) {
	         message = s + ": " + message;
	      } else {
	         message = s;
	      }

	      if (originalException != null) {
	         message = message + "\nOriginal Exception was "
	                   + originalException.toString();
	      }

	      return message;
	   }

	   /**
	    * Method printStackTrace
	    *
	    */
	   public void printStackTrace() {

	      synchronized (System.err) {
	         super.printStackTrace(System.err);

	         if (this.originalException != null) {
	            this.originalException.printStackTrace(System.err);
	         }
	      }
	   }

	   /**
	    * Method printStackTrace
	    *
	    * @param printwriter
	    */
	   public void printStackTrace(PrintWriter printwriter) {

	      super.printStackTrace(printwriter);

	      if (this.originalException != null) {
	         this.originalException.printStackTrace(printwriter);
	      }
	   }

	   /**
	    * Method printStackTrace
	    *
	    * @param printstream
	    */
	   public void printStackTrace(PrintStream printstream) {

	      super.printStackTrace(printstream);

	      if (this.originalException != null) {
	         this.originalException.printStackTrace(printstream);
	      }
	   }

	   /**
	    * Method getOriginalException
	    *
	    * @return the original exception
	    */
	   public Exception getOriginalException() {
	      return originalException;
	   }
}
