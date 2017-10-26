  <!-- Chain Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="ChainTest">Chain Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/misc/chain/package-summary.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  Java Web Parts contains a Chain Of Responsibility (CoR) pattern implementation
  that provides a great deal of flexibility to a developer.  You can define
  subchains (i.e., Commands that are themselves Chains), you can extend both
  Chains and Catalogs, can redirect Chain flow during execution, can repeat
  individual Commands or whole Chains, etc.  The demonstration below shows off
  some of this functionality.
  <br/><br/>
  Chain 1 is a demonstration of the math expression
  ((((a+1)/2)*((b+1)/2)))*x implemented as a Chain of 7 Commands, where one of
  the Commands is a subchain.
  <br/><br/>
  Chain 2 demonstrates the expression
  ((((a+1)/2)*((b+1)/2)))*x implemented as a Chain of 9 Commands where the
  Chain extends Chain 1 and overrides a Command in it.
  <br/><br/>
  Chain 3 demonstrates the
  expression ((a+1)+(b+5)) * chain_x3 where the Chain's flow is
  redirected by jumping from one Command directly to another, effectively
  skipping a Command.
  <br/><br/>
  Chain 4 demonstrates the looping.  First, 1 is added to a, then 5 is added to
  a 4 times, then a is multiplied by b.
  <br/><br/>
  <%
    // ***** Chain 1 *****
    String chain_outcome1 = "";
    int chain_actualResult1 = 0;
    int chain_a1 = 12;
    int chain_b1 = 9;
    int chain_x1 = 3;
    int chain_expectedResult1 = ((((chain_a1+1)/2)-2) * (((chain_b1+1)/2)-2)) * chain_x1;
    ChainManager chain_cm1 = new ChainManager("/chain_config.xml,/chain_config_2.xml");
    ChainContext chain_ct1 = chain_cm1.createContext();
    chain_ct1.setAttribute("a", new Integer(chain_a1));
    chain_ct1.setAttribute("b", new Integer(chain_b1));
    chain_cm1.executeChain("catalog1/chain1", chain_ct1);
    Result chain_cr1 = chain_ct1.getResult();
    if (chain_cr1.getCode() == Result.SUCCESS) {
      chain_outcome1 = "Successful chain execution";
      chain_actualResult1 = ((Integer)chain_ct1.getAttribute("answer")).intValue();
    }
    if (chain_cr1.getCode() == Result.FAIL) {
      chain_outcome1 = "Failed chain execution";
    }
    if (chain_cr1.getCode() == Result.ABORT) {
      chain_outcome1 = "Aborted chain execution";
    }
    // ***** Chain 2 *****
    String chain_outcome2 = "";
    int chain_actualResult2 = 0;
    int chain_a2 = 13;
    int chain_b2 = 11;
    int chain_x2 = 3;
    int chain_expectedResult2 = (((((chain_a2+1)/2)-2) * (((chain_b2+5)/2)-2)) * chain_x2) / 2;
    ChainManager chain_cm2 = new ChainManager();
    ChainContext chain_ct2 = chain_cm2.createContext();
    chain_ct2.setAttribute("a", new Integer(chain_a2));
    chain_ct2.setAttribute("b", new Integer(chain_b2));
    chain_cm2.executeChain("catalog2/chain2", chain_ct2);
    Result chain_cr2 = chain_ct2.getResult();
    if (chain_cr2.getCode() == Result.SUCCESS) {
      chain_outcome2 = "Successful chain execution";
      chain_actualResult2 = ((Integer)chain_ct2.getAttribute("answer")).intValue();
    }
    if (chain_cr2.getCode() == Result.FAIL) {
      chain_outcome2 = "Failed chain execution";
    }
    if (chain_cr2.getCode() == Result.ABORT) {
      chain_outcome2 = "Aborted chain execution";
    }
    // ***** Chain 3 *****
    String chain_outcome3 = "";
    int chain_actualResult3 = 0;
    int chain_a3 = 2;
    int chain_b3 = 3;
    int chain_x3 = 5;
    int chain_expectedResult3 = ((chain_a3+1)+(chain_b3+5)) * chain_x3;
    ChainManager chain_cm3 = new ChainManager();
    ChainContext chain_ct3 = chain_cm3.createContext();
    chain_ct3.setAttribute("a", new Integer(chain_a3));
    chain_ct3.setAttribute("b", new Integer(chain_b3));
    chain_cm3.executeChain("catalog2/chain3", chain_ct3);
    Result chain_cr3 = chain_ct3.getResult();
    if (chain_cr3.getCode() == Result.SUCCESS) {
      chain_outcome3 = "Successful chain execution";
      chain_actualResult3 = ((Integer)chain_ct3.getAttribute("answer")).intValue();
    }
    if (chain_cr3.getCode() == Result.FAIL) {
      chain_outcome3 = "Failed chain execution";
    }
    if (chain_cr3.getCode() == Result.ABORT) {
      chain_outcome3 = "Aborted chain execution";
    }
    // ***** Chain 4 *****
    String chain_outcome4 = "";
    int chain_actualResult4 = 0;
    int chain_a4 = 1;
    int chain_b4 = 2;
    int chain_a4_temp = chain_a4;
    int chain_b4_temp = chain_b4;
    chain_a4_temp = chain_a4_temp + 1;
    chain_b4_temp = chain_b4_temp + 5;
    chain_b4_temp = chain_b4_temp + 5;
    chain_b4_temp = chain_b4_temp + 5;
    chain_b4_temp = chain_b4_temp + 5;
    int chain_expectedResult4 = chain_a4_temp * chain_b4_temp;
    ChainManager chain_cm4 = new ChainManager();
    ChainContext chain_ct4 = chain_cm4.createContext();
    chain_ct4.setAttribute("a", new Integer(chain_a4));
    chain_ct4.setAttribute("b", new Integer(chain_b4));
    chain_cm4.executeChain("catalog1/loopChain", chain_ct4);
    Result chain_cr4 = chain_ct4.getResult();
    if (chain_cr4.getCode() == Result.SUCCESS) {
      chain_outcome4 = "Successful chain execution";
      chain_actualResult4 = ((Integer)chain_ct4.getAttribute("answer")).intValue();
    }
    if (chain_cr4.getCode() == Result.FAIL) {
      chain_outcome4 = "Failed chain execution";
    }
    if (chain_cr4.getCode() == Result.ABORT) {
      chain_outcome4 = "Aborted chain execution";
    }
  %>
  <ul>
  <li>
    Outcome of Chain 1: <%=chain_outcome1%> (extraInfo=<%=chain_cr1.getExtraInfo()%>)<br/>
    Expected result of Chain 1: <%=chain_expectedResult1%><br/>
    Actual result of Chain 1: <%=chain_actualResult1%><br/>
  </li>
  <br/>
  <li>
    Outcome of Chain 2: <%=chain_outcome2%> (extraInfo=<%=chain_cr2.getExtraInfo()%>)<br/>
    Expected result of Chain 2: <%=chain_expectedResult2%><br/>
    Actual result of Chain 2: <%=chain_actualResult2%><br/>
  </li>
  <br/>
  <li>
    Outcome of Chain 3: <%=chain_outcome3%> (extraInfo=<%=chain_cr3.getExtraInfo()%>)<br/>
    Expected result of Chain 3: <%=chain_expectedResult3%><br/>
    Actual result of Chain 3: <%=chain_actualResult3%><br/>
  </li>
  <br/>
  <li>
    Outcome of Chain 4: <%=chain_outcome4%> (extraInfo=<%=chain_cr4.getExtraInfo()%>)<br/>
    Expected result of Chain 4: <%=chain_expectedResult4%><br/>
    Actual result of Chain 4: <%=chain_actualResult4%><br/>
  </li>
  </ul>
  <br/></div>