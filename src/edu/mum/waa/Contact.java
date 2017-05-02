/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.mum.waa;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 *
 * @author USER
 */
public class Contact {
    
    
    public void sendHTML(BBHttpRequest req, Socket clientSocket){
        StringBuffer response=new StringBuffer();
        try{
        DataOutputStream out=new DataOutputStream(clientSocket.getOutputStream());
       response.append("<!DOCTYPE html>");
		response.append("<html>");
		response.append("<head>");
		response.append("<title>Almost an HTTP Server</title>");
		response.append("</head>");
		response.append("<body>");
		
	         response.append("<h1>This is response from Contact page</h1>");
                 //dynamically creat an html page for different uri
               
               
                      response.append("<h2>Contact view </h2>\r\n");
                
                
                response.append("</body>");
                response.append("</html>");                   
                
                
                String statusLine = "HTTP/1.1 200 OK" + "\r\n";
		String serverdetails = "Server: BareBones HTTPServer";
		String contentLengthLine = "Content-Length: " + response.toString().length() + "\r\n";
		String contentTypeLine = "Content-Type: text/html" + "\r\n";        
				
		out.writeBytes(statusLine);
		out.writeBytes(serverdetails);
		out.writeBytes(contentTypeLine);
		out.writeBytes(contentLengthLine);
		out.writeBytes("Connection: close\r\n");
		out.writeBytes("\r\n");             
               
                out.writeBytes(response.toString());
                out.close();
                
        }
        catch(Exception e){
            
        }
    }
    
}
