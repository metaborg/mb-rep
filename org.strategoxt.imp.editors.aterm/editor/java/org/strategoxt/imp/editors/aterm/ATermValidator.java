package org.strategoxt.imp.editors.aterm;

import org.strategoxt.imp.runtime.dynamicloading.Descriptor;
import org.strategoxt.imp.runtime.services.MetaFileLanguageValidator;

public class ATermValidator extends MetaFileLanguageValidator 
{ 
  public Descriptor getDescriptor()
  { 
    return ATermParseController.getDescriptor();
  }
}