package org.example.version2_xpresbank.Service;

import org.example.version2_xpresbank.DTO.CreateInvoiceDTO;
import org.example.version2_xpresbank.DTO.InvoiceDTO;
import org.example.version2_xpresbank.Entity.Enums.InvoiceStatus;
import org.example.version2_xpresbank.Entity.Invoice;
import org.example.version2_xpresbank.Entity.User;
import org.example.version2_xpresbank.Exception.InvoiceNotFoundException;
import org.example.version2_xpresbank.Mapper.InvoiceMapper;
import org.example.version2_xpresbank.Repository.InvoiceRepository;
import org.example.version2_xpresbank.Repository.UserRepository;
import org.example.version2_xpresbank.VM.InvoiceVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InvoiceServiceTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private InvoiceMapper invoiceMapper;

    @InjectMocks
    private InvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createInvoice_ShouldReturnInvoiceVM_WhenUserExists() {
        Long userId = 1L;
        CreateInvoiceDTO createInvoiceDTO = new CreateInvoiceDTO();
        User user = new User();
        Invoice invoice = new Invoice();
        Invoice savedInvoice = new Invoice();
        InvoiceVM invoiceVM = new InvoiceVM();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(invoiceMapper.fromCreateInvoiceDTO(createInvoiceDTO, user)).thenReturn(invoice);
        when(invoiceRepository.save(invoice)).thenReturn(savedInvoice);
        when(invoiceMapper.toInvoiceVM(savedInvoice, "Invoice created successfully")).thenReturn(invoiceVM);

        InvoiceVM result = invoiceService.createInvoice(userId, createInvoiceDTO);

        assertEquals(invoiceVM, result);
        verify(userRepository).findById(userId);
        verify(invoiceMapper).fromCreateInvoiceDTO(createInvoiceDTO, user);
        verify(invoiceRepository).save(invoice);
        verify(invoiceMapper).toInvoiceVM(savedInvoice, "Invoice created successfully");
    }

    @Test
    void createInvoice_ShouldThrowInvoiceNotFoundException_WhenUserDoesNotExist() {
        Long userId = 1L;
        CreateInvoiceDTO createInvoiceDTO = new CreateInvoiceDTO();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.createInvoice(userId, createInvoiceDTO));
        verify(userRepository).findById(userId);
        verify(invoiceMapper, never()).fromCreateInvoiceDTO(any(), any());
        verify(invoiceRepository, never()).save(any());
    }

    @Test
    void getAllInvoices_ShouldReturnListOfInvoiceDTO() {
        Invoice invoice1 = new Invoice();
        Invoice invoice2 = new Invoice();
        List<Invoice> invoices = Arrays.asList(invoice1, invoice2);
        InvoiceDTO invoiceDTO1 = new InvoiceDTO();
        InvoiceDTO invoiceDTO2 = new InvoiceDTO();

        when(invoiceRepository.findAll()).thenReturn(invoices);
        when(invoiceMapper.toInvoiceDTO(invoice1)).thenReturn(invoiceDTO1);
        when(invoiceMapper.toInvoiceDTO(invoice2)).thenReturn(invoiceDTO2);

        List<InvoiceDTO> result = invoiceService.getAllInvoices();

        assertEquals(2, result.size());
        assertEquals(invoiceDTO1, result.get(0));
        assertEquals(invoiceDTO2, result.get(1));
        verify(invoiceRepository).findAll();
        verify(invoiceMapper).toInvoiceDTO(invoice1);
        verify(invoiceMapper).toInvoiceDTO(invoice2);
    }

    @Test
    void getInvoiceById_ShouldReturnInvoiceDTO_WhenInvoiceExists() {
        Long invoiceId = 1L;
        Invoice invoice = new Invoice();
        InvoiceDTO invoiceDTO = new InvoiceDTO();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceMapper.toInvoiceDTO(invoice)).thenReturn(invoiceDTO);

        InvoiceDTO result = invoiceService.getInvoiceById(invoiceId);

        assertEquals(invoiceDTO, result);
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceMapper).toInvoiceDTO(invoice);
    }

    @Test
    void getInvoiceById_ShouldThrowInvoiceNotFoundException_WhenInvoiceDoesNotExist() {
        Long invoiceId = 1L;

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.getInvoiceById(invoiceId));
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceMapper, never()).toInvoiceDTO(any());
    }

    @Test
    void updateInvoiceStatus_ShouldReturnInvoiceVM_WhenInvoiceExists() {
        Long invoiceId = 1L;
        String status = "PAID";
        Invoice invoice = new Invoice();
        Invoice updatedInvoice = new Invoice();
        InvoiceVM invoiceVM = new InvoiceVM();

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(invoiceRepository.save(invoice)).thenReturn(updatedInvoice);
        when(invoiceMapper.toInvoiceVM(updatedInvoice, "Invoice status updated successfully")).thenReturn(invoiceVM);

        InvoiceVM result = invoiceService.updateInvoiceStatus(invoiceId, status);

        assertEquals(invoiceVM, result);
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceRepository).save(invoice);
        verify(invoiceMapper).toInvoiceVM(updatedInvoice, "Invoice status updated successfully");
    }

    @Test
    void updateInvoiceStatus_ShouldThrowInvoiceNotFoundException_WhenInvoiceDoesNotExist() {
        Long invoiceId = 1L;
        String status = "PAID";

        when(invoiceRepository.findById(invoiceId)).thenReturn(Optional.empty());

        assertThrows(InvoiceNotFoundException.class, () -> invoiceService.updateInvoiceStatus(invoiceId, status));
        verify(invoiceRepository).findById(invoiceId);
        verify(invoiceRepository, never()).save(any());
        verify(invoiceMapper, never()).toInvoiceVM(any(), any());
    }
}