package com.hhoss.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hhoss.jour.Logger;

public class FormatterTest {
  private static final Logger logger = Logger.get();

  private static void test() {
    SimpleDateFormat YYYYMMDD = new SimpleDateFormat("yyyyMMdd");
    logger.debug("1");
    for (int i = 1; i < 10000; i++) {
      YYYYMMDD.format(new Date());
    }
    logger.debug("2");
    for (int i = 1; i < 10000; i++) {
      YYYYMMDD.format(new Date());
    }
    logger.debug("3");
    for (int i = 1; i < 10000; i++) {
      Formatter.format(new Date(), "yyyyMMdd");
    }
    logger.debug("4");
    for (int i = 1; i < 10000; i++) {
      Formatter.format(new Date(), "yyyyMMdd");
    }
    logger.debug("5");
  }

  public static void main(String[] args) {
    test();

  }



}
