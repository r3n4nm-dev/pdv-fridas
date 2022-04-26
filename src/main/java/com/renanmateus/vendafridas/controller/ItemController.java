package com.renanmateus.vendafridas.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.renanmateus.vendafridas.model.Item;
import com.renanmateus.vendafridas.model.Tipo;
import com.renanmateus.vendafridas.repository.ItemRepository;

@Controller
@RequestMapping("/itens")
public class ItemController {


	
	@Autowired 
	private ItemRepository itemRepository;
	
	
	@GetMapping("/listar-itens")
	public String listarItens(ModelMap modelMap, ModelAndView modelAndView, Item item) {
		

		
		modelMap.addAttribute("pao",this.itemRepository.findByTipo(Tipo.Pão));
		modelMap.addAttribute("tapiocas",this.itemRepository.findByTipo(Tipo.Tapioca));
		modelMap.addAttribute("tapiocas_doces",this.itemRepository.findByTipo(Tipo.Tapioca_Doce));
		modelMap.addAttribute("bebidas",this.itemRepository.findByTipo(Tipo.Bebida));
		modelMap.addAttribute("bebidas_quentes",this.itemRepository.findByTipo(Tipo.Bebida_Quente));
		modelMap.addAttribute("cuscuz",this.itemRepository.findByTipo(Tipo.Cuscuz));
		modelMap.addAttribute("ovos",this.itemRepository.findByTipo(Tipo.Ovos));
		modelMap.addAttribute("caseiro",this.itemRepository.findByTipo(Tipo.Pão_Caseiro));
		modelMap.addAttribute("crepioca",this.itemRepository.findByTipo(Tipo.Crepioca));
		modelMap.addAttribute("salgado",this.itemRepository.findByTipo(Tipo.Salgado));
		modelMap.addAttribute("adicional",this.itemRepository.findByTipo(Tipo.Adicional));

		modelAndView.addObject("item", item);

		
		return "listar-itens";
	}
	
	@GetMapping("/editar/{itemId}") 
	public String editarItem(ModelMap modelMap, @PathVariable Long itemId) {
		modelMap.addAttribute("item",this.itemRepository.findById(itemId));

		return "item";
	}

	
	@PostMapping("/editar") 
	public String editar(Item item, RedirectAttributes attr) {
		
		this.itemRepository.save(item);
		attr.addFlashAttribute("edit", true);

		return "redirect:/itens/listar-itens";
	}

	
	@PostMapping("/additem") 
		public String add(@Valid Item item, BindingResult result, RedirectAttributes attr, ModelMap modelMap, ModelAndView modelAndView) {
		if (result.hasErrors()) {
		
			
			modelMap.addAttribute("pao",this.itemRepository.findByTipo(Tipo.Pão));
			modelMap.addAttribute("tapiocas",this.itemRepository.findByTipo(Tipo.Tapioca));
			modelMap.addAttribute("tapiocas_doces",this.itemRepository.findByTipo(Tipo.Tapioca_Doce));
			modelMap.addAttribute("bebidas",this.itemRepository.findByTipo(Tipo.Bebida));
			modelMap.addAttribute("bebidas_quentes",this.itemRepository.findByTipo(Tipo.Bebida_Quente));
			modelMap.addAttribute("cuscuz",this.itemRepository.findByTipo(Tipo.Cuscuz));
			modelMap.addAttribute("ovos",this.itemRepository.findByTipo(Tipo.Ovos));
			modelMap.addAttribute("caseiro",this.itemRepository.findByTipo(Tipo.Pão_Caseiro));
			modelMap.addAttribute("crepioca",this.itemRepository.findByTipo(Tipo.Crepioca));
			modelMap.addAttribute("salgado",this.itemRepository.findByTipo(Tipo.Salgado));
			modelMap.addAttribute("adicional",this.itemRepository.findByTipo(Tipo.Adicional));

			modelAndView.addObject("item", item);
			return "listar-itens";
		}
		this.itemRepository.save(item);
		attr.addFlashAttribute("add", true);

		return "redirect:/itens/listar-itens";
		}
	


@GetMapping("/deletarItem/{itemId}") 
	public String add(@PathVariable Long itemId,  RedirectAttributes attr) {
	this.itemRepository.deleteById(itemId);
	attr.addFlashAttribute("remove", true);

	return "redirect:/itens/listar-itens";
	}
}
