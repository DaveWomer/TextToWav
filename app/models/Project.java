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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.sound.sampled.AudioFileFormat.Type;

import org.joda.time.DateTime;


import play.data.format.Formats;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Project extends Model {
	
	@Id
    public Long id;
	
	public String name;
	
    @Formats.DateTime(pattern="MM/dd/yy")
	public Date created = new Date();
    
    @OneToMany(mappedBy="project", cascade=CascadeType.ALL)
    public List<Prompt> prompts;
    
    public Project(String name) {
        this.name = name;
    }
    
   
    public static Model.Finder<Long,Project> find = new Model.Finder(Long.class, Project.class);
    
	public static List<Project> all() {
		  return find.all();
	}
	
	public static void create(String name){
		
		Project project = new Project(name);
		project.save();
	}
	
	public static void delete(Long id) {
		  find.ref(id).delete();
		}

}
