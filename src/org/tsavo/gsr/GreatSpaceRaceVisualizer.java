package org.tsavo.gsr;

import org.json.JSONArray;
import org.json.JSONObject;

import processing.core.PApplet;
import processing.net.Client;

public class GreatSpaceRaceVisualizer extends PApplet {

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present",
				"org.tsavo.gsr.GreatSpaceRaceVisualizer" });
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Client client;
	float thrust = 0.0f;
	float rotation = 0.0f;

	public void setup() {
		size(displayWidth, displayHeight, P3D);
		client = new Client(this, "127.0.0.1", 8080);
	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == UP) {
				thrust = 1.0f;
			} else if (keyCode == LEFT) {
				rotation = -1.0f;
			} else if (keyCode == RIGHT) {
				rotation = 1.0f;
			}
		}
	}

	public void keyReleased() {
		if (key == CODED) {
			if (keyCode == UP) {
				thrust = 0.0f;
			} else if (keyCode == LEFT) {
				rotation = 0.0f;
			} else if (keyCode == RIGHT) {
				rotation = 0.0f;
			}
		}
	}

	public void draw() {
		background(255);
		String messageSoFar = null;
		client.write("{\"Thrust\":" + thrust + ", \"Rotation\":" + rotation
				+ "}\n");
		stroke(0, 0, 0);
		fill(255, 0, 0);
		while (messageSoFar == null) {
			try {
				messageSoFar = client.readStringUntil('\n');
				if (messageSoFar == null) {
					continue;
				}
				// System.out.println(messageSoFar);
				JSONObject scene = new JSONObject(messageSoFar);
				JSONArray track = scene.getJSONObject("track").getJSONArray(
						"Walls");
				for (int index = 0; index < track.length(); index++) {
					stroke(0, 0, 0);
					pushMatrix();
					float x1 = (float) track.optJSONObject(index)
							.getJSONArray("Point1").getDouble(0);
					float y1 = (float) track.optJSONObject(index)
							.getJSONArray("Point1").getDouble(1);
					float x2 = (float) track.optJSONObject(index)
							.getJSONArray("Point2").getDouble(0);
					float y2 = (float) track.optJSONObject(index)
							.getJSONArray("Point2").getDouble(1);
					float width = x2 - x1;
					float height = y2 - y1;
					translate(x1 + (width / 2), y1 + (height / 2));
					box(width, height, 50);
					popMatrix();
				}
				org.json.JSONArray players = scene.getJSONArray("players");
				for (int index = 0; index < players.length(); index++) {
					org.json.JSONObject player = players.optJSONObject(index);
					
					org.json.JSONArray dims = player.getJSONArray("Dimensions");
					org.json.JSONArray pos = player.getJSONArray("Position");

					float x = (float) pos.getDouble(0);
					float y = (float) pos.getDouble(1);
					float w = (float) dims.getDouble(0);
					float h = (float) dims.getDouble(1);
					float a = (float) player.getDouble("Angle");
					float px = x + cos(radians(a)) * 50;
			        float py = y + sin(radians(a)) * 50;
			        //rotate(a);
					//camera(x, 25, y, px, 25, py, 0, 1, 0);
					//rotate(0);
					pushMatrix();			
					translate(x, y);
					rotate(a);
					box(w, h, 50);
					popMatrix();
				}
			} catch (Exception e) {
			}
		}
		
	}
}