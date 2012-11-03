/* **************************************************************************************  
 * Copyright 2012 Dave Womer 
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   
 **************************************************************************************/

package models;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;




import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;


import play.data.validation.Constraints;
import play.db.ebean.Model;
import util.ByteArrayAudioPlayer;


@Entity
public class Prompt extends Model{
	
	@Id
    public Long id;
	
    @Constraints.Required
	public String filename;
	
	public String text;
	
	@Lob
	public byte[] file;
	

	
	@ManyToOne
    public Project project;
	
	public static Model.Finder<Long,Prompt> find = new Model.Finder(Long.class, Prompt.class);
	
	public static void create(Prompt prompt, Long projectId){
		prompt.project = Project.find.ref(projectId);

		prompt.file = speak(prompt);
	    prompt.file = convertFileToULAW(prompt);
		prompt.save();
	}
	
	public static List<Prompt> all() {
		  return find.all();
	}
	
    /**
     * Find prompts related to a project
     */
    public static List<Prompt> findByProject(Long project) {
        return Prompt.find.where()
            .eq("project.id", project)
            .findList();
    }
	
	public static byte[] speak(Prompt prompt) {
 	   try{
 	      System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
 	      ByteArrayAudioPlayer objPlayer = null;
 	      String voiceName = "kevin";
 	      VoiceManager voiceManager = VoiceManager.getInstance();
 	      Voice objVoice = voiceManager.getVoice(voiceName);
 	      if (objVoice == null) {
 	         System.err.println("Cannot find a voice named " + voiceName + ".  Please specify a different voice.");
 	         return null;
 	      }
 	      objVoice.allocate();
 	      
		  
		  //Bit Rate: 64 kbps
		  //Audio sample size: 8 bit
		  //Channels: 1 (mono)
		  //Audio sample rate: 8 kHz
		  //Audio format: CCITT u-Law
		  
		  AudioFormat format = new AudioFormat(Encoding.ULAW, 8000, 8,1,8,8000, true);
 	    		  
 	      objPlayer = new ByteArrayAudioPlayer(Type.WAVE);
 	      
 	      objPlayer.setAudioFormat(format);
 	      objVoice.setAudioPlayer(objPlayer);
 	      objVoice.speak(prompt.text);
 	      objVoice.deallocate();
 	      objPlayer.close();
 	      return objPlayer.getByteArray();

 	   }catch (Exception e){
 		  e.printStackTrace();
 	      return null;
 	   }
 	}
	
	private static byte[] convertFileToULAW(Prompt prompt){
    	
		ByteArrayInputStream pcmFile = new ByteArrayInputStream(prompt.file);
		ByteArrayOutputStream ulawFile = new ByteArrayOutputStream();
		AudioInputStream	ais = null;
		
		try{
			ais = AudioSystem.getAudioInputStream(pcmFile);
		}catch (Exception e){
			e.printStackTrace();
		}
		
		AudioFormat.Encoding	targetEncoding = AudioFormat.Encoding.ULAW;
		AudioInputStream	ulawAudioInputStreamAIS = AudioSystem.getAudioInputStream(targetEncoding, ais);
		AudioFileFormat.Type	fileType = AudioFileFormat.Type.WAVE;
		int	nWrittenFrames = 0;
		try{
			nWrittenFrames = AudioSystem.write(ulawAudioInputStreamAIS, fileType, ulawFile);
		}catch (IOException e){
			e.printStackTrace();
		}
		return ulawFile.toByteArray();
		
    }
	

	
	public static byte[] zipPromptsForProject(Long projectId){
		try
		{



			ByteArrayOutputStream bufOutput = new ByteArrayOutputStream();
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(bufOutput));

			
			List<Prompt> prompts = Prompt.findByProject(projectId);
			int offset = 0;
			for (int i=0; i<prompts.size(); i++)
			{
				
				ZipEntry entry = new ZipEntry(prompts.get(i).filename + ".wav");
		        entry.setSize(prompts.get(i).file.length);
		        out.putNextEntry(entry);
		        out.write(prompts.get(i).file);
			}
			out.flush();
			out.close();
			
			return bufOutput.toByteArray();
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		} 
	}
	
	public static void delete(Long id) {
		  find.ref(id).delete();
		}
	



}
