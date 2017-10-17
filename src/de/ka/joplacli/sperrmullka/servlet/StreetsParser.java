package de.ka.joplacli.sperrmullka.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.ka.joplacli.sperrmullka.dto.StreetBean;

/**
 * Servlet implementation class StreetsParser
 */
@WebServlet("/Endpoint")
public class StreetsParser extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public StreetsParser() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		List<StreetBean> completeList = new ArrayList<>();
		for(int i = 65; i < 91; i++){
			String firstChar = Character.toString ((char) i);
			String secondChar = Character.toString ((char) (i + 1));
			
			try{
				completeList.addAll(streetsByLetter(firstChar, secondChar));
				
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		PrintWriter out = new PrintWriter(response.getOutputStream(), true);
		if(completeList.size() > 0){
			int index = 0;
			for(StreetBean street:completeList){
				index++;
				out.println(String.format("%04d", index) + " - " + street.getLetter() + " - " + street.getStreet());					
			}
		}
	}
	
	private List<StreetBean> streetsByLetter(String firstLetter, String secondLetter) throws JDOMException, IOException{
		
		List<StreetBean> listStreets = new ArrayList<>();
		
		Client client = Client.create();

		WebResource webResource = client.resource("http://web3.karlsruhe.de/service/abfall/akal/akal.php?von=" + firstLetter + "&bis=" + secondLetter);

		ClientResponse webResponse = webResource.post(ClientResponse.class);

		if (webResponse.getStatus() == 200) {
			
			InputStream in = webResponse.getEntityInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			while((line = reader.readLine()) != null) {
				if(line.contains("tab_body")){

					SAXBuilder saxBuilder = new SAXBuilder(); 
					
					//Delete the last tag of the line "<TD>" and Delete "selected" attribute
					line = line.substring(0, line.length() - 7).replace("selected", "");
					
					//Insert content of the attribute value in ""
					String newLine = addValueQuotationMarks(line);
					
					//Replace "SELECT" with "select"
					newLine = newLine.replace("SELECT", "select");
					
					Document document = saxBuilder.build(new StringReader(newLine));
					List<Element> children = document.getRootElement().getChildren();
					
					for(Element child:children){
						int value = child.getAttribute("value").getIntValue();
						String content = child.getText();
						
						StreetBean streetData = new StreetBean();
						streetData.setLetter(firstLetter);
						streetData.setStreet(content);
						streetData.setValue(value);
						listStreets.add(streetData);
					}
					
					break;
				}	
			}
			
		}else{
			throw new RuntimeException("Failed : HTTP error code : "
					+ webResponse.getStatus());
		}
		
		return listStreets;
	}

	private String addValueQuotationMarks(String line) {
		if(line.indexOf("value=") != -1){		
			String beggining = line.substring(0, line.indexOf("value=") + 6) + "\"";
			
			String restLine = line.substring(line.indexOf("value=") + 6, line.length());
			
			restLine = restLine.substring(0, restLine.indexOf(">")) + "\"" + restLine.substring(restLine.indexOf(">"), restLine.length());

			return beggining + addValueQuotationMarks(restLine);
		}else{
			return line;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
