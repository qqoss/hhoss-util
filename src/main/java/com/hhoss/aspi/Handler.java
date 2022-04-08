package com.hhoss.aspi;

import java.io.Serializable;

public interface Handler<H> extends Serializable{
  String handle(H obj);
}

