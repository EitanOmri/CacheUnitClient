package com.hit.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class CacheUnitClient extends Object {

	private final static String HOST = "localhost";
	private final static int PORT = 12345;

	public CacheUnitClient() {
	}

	@SuppressWarnings("unchecked")
	public String send(String request) {
		try {
			Socket socket = new Socket(HOST, PORT);
			ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
			os.writeObject(request);
			os.flush();
			HashMap<String, String> hashMapAnswer = (HashMap<String, String>) is.readObject();
			is.close();
			os.close();
			socket.close();
			String answer = hashMapAnswer.get("action") + "-" + hashMapAnswer.get("Succeeded");
			if (hashMapAnswer.get("statistics") != null)
				answer += "-" + hashMapAnswer.get("statistics");
			if (hashMapAnswer.get("results") != null)
				answer += "-" + hashMapAnswer.get("results");
			return answer;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
