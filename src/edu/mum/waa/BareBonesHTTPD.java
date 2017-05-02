package edu.mum.waa;

import java.io.*;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;

public class BareBonesHTTPD extends Thread {

	private static final int PortNumber = 8090;

	Socket connectedClient = null;

	public BareBonesHTTPD(Socket client) {
		connectedClient = client;
	}

	public void run() {

		try {
                    
                    
			
                   
                   
                
                
                    
                    
                    
                    System.out.println(connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

			BBHttpRequest httpRequest = getRequest(connectedClient.getInputStream());
			 BBHttpResponse httpResponse;
                         
              //                         USing Configuration file
//                         String classname=configuration(httpRequest.getUri());                         
//                         Class obj=Class.forName(classname); 
//                          Method method=obj.getMethod("sendHTML", edu.mum.waa.BBHttpRequest.class, java.net.Socket.class);
//                          method.invoke(obj.newInstance(), httpRequest, connectedClient);
                      
                                                 
          
                       if(httpRequest.getUri().equals("/")){
                           httpResponse= new BBHttpResponse();
			processRequest(httpRequest, httpResponse);       
                   			sendResponse(httpResponse);
                               }
                        else if(httpRequest.getUri().equals("/home/index.html")){
                            httpResponse= new BBHttpResponse();
                            readFile(httpRequest.getUri(),httpResponse);
                            sendResponse(httpResponse);
                        }
                        else if(httpRequest.getUri().equals("/welcome.web")||httpRequest.getUri().equals("/contacts.web")){
                            
                            Welcome welcome=new Welcome();
                            welcome.sendHTML(httpRequest,connectedClient);
                        }
                        
                        else if(httpRequest.getUri().equals("/contact.web")){                            
                            Contact contact=new Contact();
                           contact.sendHTML(httpRequest,connectedClient);
                        }              
                   
                   
                   
                   

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
        
        public String configuration(String uri){
            System.out.println("uriiiiiiiiiiiii"+uri);
            
            BufferedReader reader=null;
              String result="";
            try{
                URL url=getClass().getResource("configuration.txt");
                File file=new File(url.getPath());
                reader=new BufferedReader(new FileReader(file));                          
                
                String line;              
                StringTokenizer token;
                while((line=reader.readLine())!=null){          
                
               token=new StringTokenizer(line);
                   if(token.nextToken().equals(uri)){                       
                      
                   result=token.nextToken();
                   }                 
                    
                }
                
            }
            catch(Exception e){
                
            }
            
            return  result;
           
        }

        public void readFile(String uri,BBHttpResponse httpResponse){            
    
             BufferedReader reader=null;
              StringBuilder strBuff=new StringBuilder();
             try{             
               
                 URL url = getClass().getResource("index.html");             
                     File file = new File(url.getPath());
                 reader=new BufferedReader(new FileReader(file));
                 
                 System.out.println(url.getPath());
              
                 String line;
                 while((line=reader.readLine())!=null){                     
                     strBuff.append(line+"\n");                     
                 }
                 httpResponse.setStatusCode(200);
               httpResponse.setMessage(strBuff.toString());
                
             }
             catch(IOException e){
                 
             }
     
        }
	private void processRequest(BBHttpRequest httpRequest, BBHttpResponse httpResponse) {

		StringBuilder response = new StringBuilder();
		response.append("<!DOCTYPE html>");
		response.append("<html>");
		response.append("<head>");
		response.append("<title>Almost an HTTP Server</title>");
		response.append("</head>");
		response.append("<body>");
		response.append("<h1>This is the HTTP Server</h1>");
		response.append("<h2>Your request was:</h2>\r\n");
		response.append("<h3>Request Line:</h3>\r\n");
		response.append(httpRequest.getStartLine());
		response.append("<br />");
		response.append("<h3> Header Fields: </h3>");
		for (String headerField : httpRequest.getFields()) {
			response.append(headerField.replace("<", "&lt;").replace("&", "&amp;"));
			response.append("<br />");
		}
		response.append("<h3> Payload: </h3>");
		for (String messageLine : httpRequest.getMessage()) {
			response.append(messageLine.replace("<", "&lt;").replace("&", "&amp;"));
			response.append("<br />");
		}
		response.append("</body>");
		response.append("</html>");

		httpResponse.setStatusCode(200);
		httpResponse.setMessage(response.toString());
	}

	private BBHttpRequest getRequest(InputStream inputStream) throws IOException {

		BBHttpRequest httpRequest = new BBHttpRequest();

		BufferedReader fromClient = new BufferedReader(new InputStreamReader(inputStream));

		String requestString = fromClient.readLine();
		String headerLine = requestString;

		System.out.println("The HTTP request is ....");
		System.out.println(requestString);

		// Header Line
		StringTokenizer tokenizer = new StringTokenizer(headerLine);
		httpRequest.setMethod(tokenizer.nextToken());
		httpRequest.setUri(tokenizer.nextToken());
		httpRequest.setHttpVersion(tokenizer.nextToken());

		// Header Fields and Body
		boolean readingBody = false;
		ArrayList<String> fields = new ArrayList<>();
		ArrayList<String> body = new ArrayList<>();

		while (fromClient.ready()) {

			requestString = fromClient.readLine();
			System.out.println(requestString);

			if (!requestString.isEmpty()) {
				if (readingBody) {
					body.add(requestString);
				} else {
					fields.add(requestString);
				}
			} else {
				readingBody = true;
			}
		}
		httpRequest.setFields(fields);
		httpRequest.setMessage(body);
		return httpRequest;
	}

	private void sendResponse(BBHttpResponse response) throws IOException {

		String statusLine = null;
                DataOutputStream toClient = new DataOutputStream(connectedClient.getOutputStream());
		if (response.getStatusCode() == 500) {
			statusLine = "HTTP/1.1 501 Not Implemented" + "\r\n";
                        toClient.writeBytes(statusLine);
                        toClient.writeBytes("Error: Status code 500");
                        toClient.close();
		} else {
			
		statusLine = "HTTP/1.1 200 OK" + "\r\n";

		String serverdetails = "Server: BareBones HTTPServer";
		String contentLengthLine = "Content-Length: " + response.getMessage().length() + "\r\n";
		String contentTypeLine = "Content-Type: " + response.getContentType() + "\r\n";

		
                
		toClient.writeBytes(statusLine);
		toClient.writeBytes(serverdetails);
		toClient.writeBytes(contentTypeLine);
		toClient.writeBytes(contentLengthLine);
		toClient.writeBytes("Connection: close\r\n");
		toClient.writeBytes("\r\n");
		toClient.writeBytes(response.getMessage());
                toClient.close();
                }

		
	}

	public static void main(String args[]) throws Exception {

		ServerSocket Server = new ServerSocket(PortNumber, 10, InetAddress.getByName("127.0.0.1"));
		System.out.println("Server Started on port " + PortNumber);

		while (true) {
			Socket connected = Server.accept();
			(new BareBonesHTTPD(connected)).start();
		}
	}
}
