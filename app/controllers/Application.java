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

package controllers;

import models.Project;
import models.Prompt;
import play.*;
import play.data.*;
import play.mvc.*;

import views.html.*;

public class Application extends Controller {
	
	static Form<Prompt> promptForm = form(Prompt.class);
	static Form<Project> projectForm = form(Project.class);
  
  public static Result index() {
    return ok(index.render());
  }
  
  public static Result generatePrompts(Long projectId){
	  Form<Prompt> filledForm = promptForm.bindFromRequest();
	  if(filledForm.hasErrors()) {
	    return ok(prompt.render(Project.find.ref(projectId), promptForm, Prompt.findByProject(projectId)));

	  } else {
	    Prompt.create(filledForm.get(), projectId);
	    return redirect(routes.Application.generatePrompts(projectId)); 


	    
	  }
  }
  
  public static Result listPrompts(Long projectId){
	  return ok(
			    views.html.prompt.render(Project.find.byId(projectId), promptForm, Prompt.findByProject(projectId))
			
			  );
  }
  
  public static Result createProject() {
	  Form<Project> filledForm = projectForm.bindFromRequest();
	  if(filledForm.hasErrors()) {
	    return badRequest(
	      views.html.project.render(filledForm, Project.all())
	    );
	  } else {
	    Project.create(filledForm.get().name);
	    return redirect(routes.Application.projects());  
	  }
  }
  
  public static Result projects(){
	  return ok(
			    views.html.project.render(projectForm, Project.all())
			
			  );
  }
  
  public static Result prompts(){
	  return ok(
			    views.html.output.render(Prompt.all())
			
			  );
  }
  
  public static Result 	getPromptWavFile(Long promptId){
	  Prompt prompt = Prompt.find.byId(promptId);
	  response().setContentType("audio/wav");
	  response().setHeader("Content-Disposition", "attachment; filename=" + prompt.filename + ".wav");
	  return ok(Prompt.find.byId(promptId).file);
  }
  
  public static Result 	getZipOfProjectFiles(Long projectId){
	  
	  response().setContentType("application/zip");
	  response().setHeader("Content-Disposition", "attachment; filename=ProjectAudio.zip");
	  return ok(Prompt.zipPromptsForProject(projectId));
  }
  
  public static Result deleteProject(Long id) {
	  Project.delete(id);
	  return redirect(routes.Application.projects());
  }
  
  public static Result deletePromptFromProject(Long promptId, Long projectId) {
	  Prompt.delete(promptId);
	  return redirect(routes.Application.listPrompts(projectId));
  }
  

  
  
}