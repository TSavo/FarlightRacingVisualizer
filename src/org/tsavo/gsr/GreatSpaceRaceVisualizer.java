package org.tsavo.gsr;

import org.json.JSONArray;
import org.json.JSONException;
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
	float[][][] track;
	float[][] goal;
	float[][][] checkpoints;

	public void setup() {
		size(displayWidth, displayHeight, P3D);
		client = new Client(this, "127.0.0.1", 8080);
		while (!client.active()) {

		}
		client.write("{\"Name\":\"Kevlar\", \"Track\":\"Test Track\", \"Prototype\":\"Test\", \"NumPlayers\":1}\n");
		String messageSoFar = null;
		while (messageSoFar == null) {
			messageSoFar = client.readStringUntil('\n');
		}

		JSONObject scene;
		try {
			System.out.print(messageSoFar);
			scene = new JSONObject(messageSoFar).getJSONObject("Data");
			JSONArray trackData = scene.getJSONObject("Track").getJSONArray(
					"Walls");
			JSONObject goalData = scene.getJSONObject("Track").getJSONObject(
					"GoalLine");
			JSONArray checkpointData = scene.getJSONObject("Track")
					.getJSONArray("Checkpoints");

			track = new float[trackData.length()][2][2];
			for (int index = 0; index < trackData.length(); index++) {
				float x1 = (float) trackData.optJSONObject(index)
						.getJSONArray("Point1").getDouble(0);
				float y1 = (float) trackData.optJSONObject(index)
						.getJSONArray("Point1").getDouble(1);
				float x2 = (float) trackData.optJSONObject(index)
						.getJSONArray("Point2").getDouble(0);
				float y2 = (float) trackData.optJSONObject(index)
						.getJSONArray("Point2").getDouble(1);
				track[index][0][0] = x1;
				track[index][0][1] = y1;
				track[index][1][0] = x2;
				track[index][1][1] = y2;
			}
			checkpoints = new float[checkpointData.length()][2][2];
			for (int index = 0; index < checkpointData.length(); index++) {
				float x1 = (float) checkpointData.optJSONObject(index)
						.getJSONObject("Wall").getJSONArray("Point1")
						.getDouble(0);
				float y1 = (float) checkpointData.optJSONObject(index)
						.getJSONObject("Wall").getJSONArray("Point1")
						.getDouble(1);
				float x2 = (float) checkpointData.optJSONObject(index)
						.getJSONObject("Wall").getJSONArray("Point2")
						.getDouble(0);
				float y2 = (float) checkpointData.optJSONObject(index)
						.getJSONObject("Wall").getJSONArray("Point2")
						.getDouble(1);
				checkpoints[index][0][0] = x1;
				checkpoints[index][0][1] = y1;
				checkpoints[index][1][0] = x2;
				checkpoints[index][1][1] = y2;
			}
			goal = new float[2][2];
			float x1 = (float) goalData.getJSONObject("Wall")
					.getJSONArray("Point1").getDouble(0);
			float y1 = (float) goalData.getJSONObject("Wall")
					.getJSONArray("Point1").getDouble(1);
			float x2 = (float) goalData.getJSONObject("Wall")
					.getJSONArray("Point2").getDouble(0);
			float y2 = (float) goalData.getJSONObject("Wall")
					.getJSONArray("Point2").getDouble(1);
			goal[0][0] = x1;
			goal[0][1] = y1;
			goal[1][0] = x2;
			goal[1][1] = y2;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void keyPressed() {
		if (key == CODED) {
			if (keyCode == UP) {
				thrust = 1.0f;
			} else if (keyCode == DOWN) {
				thrust = -1.0f;
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
			} else if (keyCode == DOWN) {
				thrust = 0.0f;
			} else if (keyCode == LEFT) {
				rotation = 0.0f;
			} else if (keyCode == RIGHT) {
				rotation = 0.0f;
			}
		}
	}

	public void renderWall(float x1, float y1, float x2, float y2, float h) {
		pushMatrix();
		float width = x2 - x1;
		float height = y2 - y1;
		translate(x1 + (width / 2), y1 + (height / 2));
		box(width, height, h);
		popMatrix();
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
				// System.out.print(messageSoFar);
				JSONObject scene = new JSONObject(messageSoFar);
				for (int index = 0; index < track.length; index++) {
					stroke(0, 0, 0);
					fill(255, 0, 0);
					renderWall(track[index][0][0], track[index][0][1],
							track[index][1][0], track[index][1][1], 50);
				}
//				for (int index = 0; index < checkpoints.length; index++) {
//					stroke(0, 0, 0);
//					fill(0, 0, 0, 0);
//					renderWall(checkpoints[index][0][0],
//							checkpoints[index][0][1], checkpoints[index][1][0],
//							checkpoints[index][1][1], 5);
//				}
				fill(0, 0, 0, 0);
				renderWall(goal[0][0], goal[0][1], goal[1][0], goal[1][1], 10);
				fill(0, 0, 255);
				org.json.JSONArray players = scene.getJSONArray("Data");
				for (int index = 0; index < players.length(); index++) {
					org.json.JSONObject player = players.optJSONObject(index);

					org.json.JSONArray dims = player.getJSONArray("Dimensions");
					org.json.JSONArray pos = player.getJSONArray("Position");

					float x = (float) pos.getDouble(0);
					float y = (float) pos.getDouble(1);
					float w = (float) dims.getDouble(0);
					float h = (float) dims.getDouble(1);
					float a = (float) player.getDouble("Angle");
					// float px = x + cos(radians(a)) * 50;
					// float py = y + sin(radians(a)) * 50;
					// rotate(a);
					// camera(x, 25, y, px, 25, py, 0, 1, 0);
					// rotate(0);
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