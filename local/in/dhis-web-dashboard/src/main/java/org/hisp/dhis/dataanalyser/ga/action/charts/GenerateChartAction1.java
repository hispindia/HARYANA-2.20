/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.dataanalyser.ga.action.charts;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.dataanalyser.ga.charts.AreaChart;
import org.hisp.dhis.dataanalyser.ga.charts.Horizontal3DBarChart;
import org.hisp.dhis.dataanalyser.ga.charts.LineAndBarChart;
import org.hisp.dhis.dataanalyser.ga.charts.LineChart;
import org.hisp.dhis.dataanalyser.ga.charts.StandardChart;
import org.hisp.dhis.dataanalyser.ga.charts.SurveyChart;
import org.hisp.dhis.dataanalyser.ga.charts.Vertical3DBarChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
/**
 * @author Mithilesh Kumar Thakur
 *
 * @version GenerateChartAction1.java Oct 27, 2010 1:17:24 PM
 */


public class GenerateChartAction1
    implements Action
{
    double[][] data1;

   // double[][] data2;

    String[] series1;

   // String[] series2;

    String[] categories1;

   // String[] categories2;

    String chartTitle;

    String xAxis_Title;

    String yAxis_Title;

    private StandardChart selChart;
    
   // private Vertical3DBarChart selChart;
    
    private HttpSession session;
    
    public HttpSession getSession()
    {
        return session;
    }
    
    private JFreeChart chart;
        
    public JFreeChart getChart()
    {
        return chart;
    }

    private String headingInfo;

    public String getHeadingInfo()
    {
        return headingInfo;
    }
    
    private List<String> yseriesList;
    
    public List<String> getYseriesList()
    {
        return yseriesList;
    }

    private List<String> xseriesList;
    
    public List<String> getXseriesList()
    {
        return xseriesList;
    }

    private List<List<String>> dataList;
          
    public List<List<String>> getDataList()
    {
        return dataList;
    }
    
    /*
    private HttpServletResponse response;
    
    public HttpServletResponse getResponse()
    {
        return response;
    }


    public void setResponse( HttpServletResponse response )
    {
        this.response = response;
    }
    */
    

    public String execute()
        throws Exception
    {

        xseriesList = new ArrayList<String>();
        yseriesList = new ArrayList<String>();
        dataList = new ArrayList<List<String>>();
        
        ActionContext ctx = ActionContext.getContext();
        HttpServletRequest req = (HttpServletRequest) ctx.get( ServletActionContext.HTTP_REQUEST );
        String currentChart = req.getParameter( "currentChart" );
        String chartDisplayOption = req.getParameter( "chartDisplayOption" );
      
        //System.out.println("chartDisplayOption : "+chartDisplayOption);
        session = req.getSession();
        Double[][] objData1 = (Double[][]) session.getAttribute( "data1" );
       // Double[][] objData2 = (Double[][]) session.getAttribute( "data2" );
                

        String[] series1S = (String[]) session.getAttribute( "series1" );
      //  String[] series2S = (String[]) session.getAttribute( "series2" );
        String[] categories1S = (String[]) session.getAttribute( "categories1" );
       // String[] categories2S = (String[]) session.getAttribute( "categories2" );
                        
       // series2 = (String[]) session.getAttribute( "series2" );
        
        chartTitle = (String) session.getAttribute( "chartTitle" );
        xAxis_Title = (String) session.getAttribute( "xAxisTitle" );
        yAxis_Title = (String) session.getAttribute( "yAxisTitle" );

        initialzeAllLists( series1S,  categories1S );
      
        //System.out.println( "\n data1 : " + objData1 );
        //System.out.println( "\n series1 : " + series1S );
        //System.out.println( "\n categories1 : " + categories1S );
        //System.out.println( "\n chartTitle : " + chartTitle );
        //System.out.println( "\n xAxisTitle : " + xAxis_Title );
        //System.out.println( "\n yAxisTitle : " + yAxis_Title );
       
        
        if( objData1 == null || series1 == null ||  categories1 == null ||  chartTitle == null || xAxis_Title == null || yAxis_Title == null )
        {
            System.out.println("Session Objects are null");
        }
        
        else
        {
            System.out.println("Session Objects are not null");
        }
                
        data1 = convertDoubleTodouble( objData1 );
       // data2 = convertDoubleTodouble( objData2 );
        
        if(chartDisplayOption == null || chartDisplayOption.equalsIgnoreCase("none")) { }
        else if(chartDisplayOption.equalsIgnoreCase("ascend")) { sortByAscending(); }
        else if(chartDisplayOption.equalsIgnoreCase("desend")) { sortByDesscending(); }
        else if(chartDisplayOption.equalsIgnoreCase("alphabet")) { sortByAlphabet(); }          
        
        initializeDataLists();
        
        //System.out.println( "current chart Type is : " + currentChart + " ,And Chart Display Option is : " + chartDisplayOption );
        
        if ( currentChart == null )
        {
            System.out.println( "current chart is null" );
            currentChart = "Vertical3DBarChart";
        }

        else if ( currentChart.equals( "Vertical3DBarChart" ) )
        {
            System.out.println( "Vertical3DBarChart" );
            selChart = new Vertical3DBarChart();
        }
       else if ( currentChart.equals( "Horizontal3DBarChart" ) )
        {
            System.out.println( "\n\n Horizontal3DBarChart" );
            selChart = new Horizontal3DBarChart();
        }
        else if ( currentChart.equals( "LineChart" ) )
        {
            System.out.println( "\n\n LineChart" );
            selChart = new LineChart();
        }
        else if ( currentChart.equals( "LineAndBarChart" ) )
        {
            System.out.println( "\n\n LineAndBarChart" );
            selChart = new LineAndBarChart();
        }
        
        else if ( currentChart.equals( "AreaChart" ) )
        {
            System.out.println( "\n\n AreaChart" );
            selChart = new AreaChart();
        }
/*        else if ( currentChart.equals( "DualAxisChart" ) )
        {
            //System.out.println( "DualAxisChart" );
            selChart = new DualAxisChart();
        }
     

        else if ( currentChart.equals( "PieChart3D" ) )
        {
            //System.out.println( "PieChart3D" );
            selChart = new PieChart3D();
        }
*/         
        else if ( currentChart.equals( "SurveyChart" ) )
        {
            //System.out.println( "SurveyChart" );
            selChart = new SurveyChart();
        }

       // chart = selChart.getChartViewer( data1, series1, categories1, chartTitle, xAxis_Title, yAxis_Title );
        chart = selChart.getChartViewergetChartViewerDataElement( data1, series1, categories1, chartTitle, xAxis_Title, yAxis_Title );

        
        // Saving chart into Session        
        ChartRenderingInfo info = null;        
        try 
        {
          info = new ChartRenderingInfo(new StandardEntityCollection());
          BufferedImage chartImage = chart.createBufferedImage(700, 500, info);
          session.setAttribute("chartImage", chartImage);
          
          session.setAttribute( "headingInfo", headingInfo );
                    
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
        
        //response.setContentType("image/png");
        
        return SUCCESS;
    }// execute end

    
    public void initializeDataLists()
    {
        int i;
        headingInfo = "<table border=\"1\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse: collapse; border-style: dotted\" bordercolor=\"#111111\" width=\"100%\"><tr><td class=\"TableHeadingCellStyles\" style=\"border-style: dotted; border-width: 1\">Service Name</td>";
        
        for(i=0; i < categories1.length; i++)
        {
            headingInfo += "<td class=\"TableHeadingCellStyles\" align=\"center\" style=\"border-style: dotted; border-width: 1\">"+categories1[i]+"</td>";
        }
        headingInfo +="</tr>";
        
        for(i=0; i<data1.length; i++)
        {
            headingInfo += "<tr><td class=\"TableHeadingCellStyles\" style=\"border-style: dotted; border-width: 1\">"+series1[i]+"</td>";
            for(int j=0; j<data1[i].length; j++)
            {
                headingInfo += "<td class=\"TableDataCellStyles\" align=\"center\" style=\"border-style: dotted; border-width: 1\">"+data1[i][j]+"</td>";                                         
            }
        }
        
        headingInfo += "</table>";
    }
    
    public void initialzeAllLists(String[]series1S,  String[] categories1S )
    
    {
        int i;
        series1 = new String[series1S.length];
       // series2 = new String[series2S.length];
        categories1 = new String[categories1S.length];
       // categories2 = new String[categories2S.length];
        
        for(i = 0; i < series1S.length; i++)
        {
                series1[i] = series1S[i];
        }
/*
        for(i = 0; i < series2S.length; i++)
        {
                series2[i] = series2S[i];
        }
*/        
        for(i = 0; i < categories1S.length; i++)
        {
                categories1[i] = categories1S[i];
        }
/*       
        for(i = 0; i < categories2S.length; i++)
        {
                categories2[i] = categories2S[i];
        }
*/      
    }
    
    public double[][] convertDoubleTodouble( Double[][] objData )
    {
        //System.out.println("Before Sorting : ");
        //double[][] data = new double[series1.length][categories1.length];
        double[][] data = new double[objData.length][objData[0].length];
        for ( int i = 0; i < objData.length; i++ )
        {
            for ( int j = 0; j < objData[0].length; j++ )
            {
               // System.out.println(objData[i][j]);
                data[i][j] = objData[i][j].doubleValue();
                //System.out.print(categories1[j]+": "+data[i][j]+", ");                
            }
            //System.out.println("");
        }

        return data;
    }// convertDoubleTodouble end

    public void sortByAscending()
    {
        for(int i=0; i < categories1.length-1 ; i++)
        {
                for(int j=0; j < categories1.length-1-i; j++)
                {
                        if(data1[0][j] > data1[0][j+1])
                        {
                                for(int k=0; k<series1.length; k++)
                                {
                                        double temp1 = data1[k][j];
                                        data1[k][j] = data1[k][j+1];
                                        data1[k][j+1] = temp1;                                          
                                }
                                
                                String temp2 = categories1[j];
                                categories1[j] = categories1[j+1];
                                categories1[j+1] = temp2;
                        }
                }
        }
/*        
        for(int i=0; i < categories2.length-1 ; i++)
        {
                for(int j=0; j < categories2.length-1-i; j++)
                {
                        if(data2[0][j] > data2[0][j+1])
                        {
                                for(int k=0; k<series2.length; k++)
                                {
                                        double temp1 = data2[k][j];
                                        data2[k][j] = data2[k][j+1];
                                        data2[k][j+1] = temp1;                                          
                                }
                                
                                String temp2 = categories2[j];
                                categories2[j] = categories2[j+1];
                                categories2[j+1] = temp2;
                        }
                }
        }
*/        
    }

    public void sortByDesscending()
    {
        for(int i=0; i < categories1.length-1 ; i++)
        {
                for(int j=0; j < categories1.length-1-i; j++)
                {
                        if(data1[0][j] < data1[0][j+1])
                        {
                                for(int k=0; k<series1.length; k++)
                                {
                                        double temp1 = data1[k][j];
                                        data1[k][j] = data1[k][j+1];
                                        data1[k][j+1] = temp1;                                          
                                }
                                
                                String temp2 = categories1[j];
                                categories1[j] = categories1[j+1];
                                categories1[j+1] = temp2;
                        }
                }
        }
 /*       
        for(int i=0; i < categories2.length-1 ; i++)
        {
                for(int j=0; j < categories2.length-1-i; j++)
                {
                        if(data2[0][j] < data2[0][j+1])
                        {
                                for(int k=0; k<series2.length; k++)
                                {
                                        double temp1 = data2[k][j];
                                        data2[k][j] = data2[k][j+1];
                                        data2[k][j+1] = temp1;                                          
                                }
                                
                                String temp2 = categories2[j];
                                categories2[j] = categories2[j+1];
                                categories2[j+1] = temp2;
                        }
                }
        }
*/
    }   
        public void sortByAlphabet()
        {
                for(int i=0; i < categories1.length-1 ; i++)
                {
                        for(int j=0; j < categories1.length-1-i; j++)
                        {
                                if(categories1[j].compareToIgnoreCase(categories1[j+1]) > 0)
                                {
                                        for(int k=0; k<series1.length; k++)
                                        {
                                        double temp1 = data1[k][j];
                                        data1[k][j] = data1[k][j+1];
                                        data1[k][j+1] = temp1;                                          
                                        }
                                        
                                        String temp2 = categories1[j];
                                        categories1[j] = categories1[j+1];
                                        categories1[j+1] = temp2;
                                }
                        }
                }
                
/*                for(int i=0; i < categories2.length-1 ; i++)
                {
                        for(int j=0; j < categories2.length-1-i; j++)
                        {
                                if(categories2[j].compareToIgnoreCase(categories2[j+1]) > 0)
                                {
                                        for(int k=0; k<series2.length; k++)
                                        {
                                        double temp1 = data2[k][j];
                                        data2[k][j] = data2[k][j+1];
                                        data2[k][j+1] = temp1;                                          
                                        }
                                        
                                        String temp2 = categories2[j];
                                        categories2[j] = categories2[j+1];
                                        categories2[j+1] = temp2;
                                }
                        }
                }
 */       
    }


    
}// class end
