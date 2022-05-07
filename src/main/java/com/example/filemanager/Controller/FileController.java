package com.example.filemanager.Controller;

import com.azure.core.annotation.Get;
import com.azure.core.annotation.Post;
import com.example.filemanager.Service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("files")
public class FileController {

    @Autowired
    FileService fileService;

    @GetMapping(value = {"/listar","/",""})
    public String listarArchivos(Model model){
        model.addAttribute("nombreArchivos",fileService.listarArchivos());
        return "lista";
    }

    @GetMapping("/ver")
    public String verArchivo(@RequestParam("nombre") String nombre,Model model){
        model.addAttribute("archivo",fileService.obtenerUrl(nombre));
        return "vista";
    }

    @GetMapping("/subida")
    public String mostrarSubida(){
        return "subir";
    }

    @PostMapping("/subir")
    public String subirArchivos(@RequestParam("archivo")MultipartFile file, RedirectAttributes attr){
        if(fileService.subirArchivo(file)){
            attr.addFlashAttribute("msg","Archivo subido exitosamente");
        }else{
            attr.addAttribute("alert","El archivo"+file.getOriginalFilename()+"No se pude subir de manera correcta");
        }
        return "redirect:/files/listar";
    }

    @GetMapping("/eliminar")
    public String eliminarArchivo(@RequestParam("nombre") String nombre, RedirectAttributes attr){
        if(fileService.eliminarArchivo(nombre)){
            attr.addFlashAttribute("msg","Archivo eliminado exitosamente");
        }else{
            attr.addFlashAttribute("alert","No se ha podido eliminar el archivo");
        }
        return "redirect:/files/listar";
    }

    @GetMapping("/descargar")
    public ResponseEntity<Resource> descargarArchivo(@RequestParam("nombre") String nombre){
        ByteArrayResource resource = new ByteArrayResource(fileService.descargarArchivo(nombre).toByteArray());
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\""+nombre+"\"").contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }
}
