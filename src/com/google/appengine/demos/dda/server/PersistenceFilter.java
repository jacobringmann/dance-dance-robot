package com.google.appengine.demos.dda.server;

import javax.servlet.*;
import java.io.IOException;

/**
 * Ensures we properly release persistence resources even in
 * the face of exceptions.
 *
 * @author Toby Reyelts
 *         Copyright May 19, 2009
 */
public class PersistenceFilter implements javax.servlet.Filter {
  public void destroy() {
  }

  public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
      throws ServletException, IOException {
    try {
      chain.doFilter(req, resp);
    } finally {
      JdoUtil.closePm();
    }
  }

  public void init(FilterConfig config) throws ServletException {
  }
}