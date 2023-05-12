package com.renanmateus.vendafridas.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
		

		
		modelMap.addAttribute("pao",this.itemRepository.findByTipoAndDeleted(Tipo.Pão, false));
		modelMap.addAttribute("tapiocas",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca, false));
		modelMap.addAttribute("tapiocas_doces",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca_Doce, false));
		modelMap.addAttribute("bebidas",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida, false));
		modelMap.addAttribute("bebidas_quentes",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida_Quente, false));
		modelMap.addAttribute("cuscuz",this.itemRepository.findByTipoAndDeleted(Tipo.Cuscuz, false));
		modelMap.addAttribute("ovos",this.itemRepository.findByTipoAndDeleted(Tipo.Ovos, false));
		modelMap.addAttribute("caseiro",this.itemRepository.findByTipoAndDeleted(Tipo.Pão_Caseiro, false));
		modelMap.addAttribute("crepioca",this.itemRepository.findByTipoAndDeleted(Tipo.Crepioca, false));
		modelMap.addAttribute("salgado",this.itemRepository.findByTipoAndDeleted(Tipo.Salgado, false));
		modelMap.addAttribute("adicional",this.itemRepository.findByTipoAndDeleted(Tipo.Adicional, false));

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
		
			
			modelMap.addAttribute("pao",this.itemRepository.findByTipoAndDeleted(Tipo.Pão, false));
			modelMap.addAttribute("tapiocas",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca, false));
			modelMap.addAttribute("tapiocas_doces",this.itemRepository.findByTipoAndDeleted(Tipo.Tapioca_Doce, false));
			modelMap.addAttribute("bebidas",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida, false));
			modelMap.addAttribute("bebidas_quentes",this.itemRepository.findByTipoAndDeleted(Tipo.Bebida_Quente, false));
			modelMap.addAttribute("cuscuz",this.itemRepository.findByTipoAndDeleted(Tipo.Cuscuz, false));
			modelMap.addAttribute("ovos",this.itemRepository.findByTipoAndDeleted(Tipo.Ovos, false));
			modelMap.addAttribute("caseiro",this.itemRepository.findByTipoAndDeleted(Tipo.Pão_Caseiro, false));
			modelMap.addAttribute("crepioca",this.itemRepository.findByTipoAndDeleted(Tipo.Crepioca, false));
			modelMap.addAttribute("salgado",this.itemRepository.findByTipoAndDeleted(Tipo.Salgado, false));
			modelMap.addAttribute("adicional",this.itemRepository.findByTipoAndDeleted(Tipo.Adicional, false));

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
