package meli.dh.com.finalmeliproject.service.shoppingCart;

import meli.dh.com.finalmeliproject.dto.payment.CreditCardDTO;
import meli.dh.com.finalmeliproject.dto.payment.PaymentDTO;
import meli.dh.com.finalmeliproject.dto.payment.PixDTO;
import meli.dh.com.finalmeliproject.dto.payment.ResponsePaymentDTO;
import meli.dh.com.finalmeliproject.dto.shoppingCart.ProductPurchaseOrderDto;
import meli.dh.com.finalmeliproject.dto.shoppingCart.PurchaseOrderDto;
import meli.dh.com.finalmeliproject.dto.shoppingCart.ResponseShoppingCartDto;
import meli.dh.com.finalmeliproject.exception.BadRequestExceptionImp;
import meli.dh.com.finalmeliproject.exception.UnauthorizedExceptionImp;
import meli.dh.com.finalmeliproject.model.*;
import meli.dh.com.finalmeliproject.model.enums.OrderStatus;
import meli.dh.com.finalmeliproject.model.payment.CreditCardPayment;
import meli.dh.com.finalmeliproject.model.payment.PixPayment;
import meli.dh.com.finalmeliproject.repository.IProductShoppingCartRepo;
import meli.dh.com.finalmeliproject.repository.IPurchaseOrderRepo;
import meli.dh.com.finalmeliproject.repository.IShoppingCartRepo;
import meli.dh.com.finalmeliproject.service.buyer.IBuyerService;
import meli.dh.com.finalmeliproject.service.payment.IPaymentService;
import meli.dh.com.finalmeliproject.service.product.IProductService;
import meli.dh.com.finalmeliproject.service.wareHouse.IWareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShoppingCartService implements IShoppingCartService {

    @Autowired
    private IProductService productService;

    @Autowired
    private IShoppingCartRepo shoppingCartRepo;

    @Autowired
    private IProductShoppingCartRepo productShoppingCartRepo;

    @Autowired
    private IPurchaseOrderRepo purchaseOrderRepo;

    @Autowired
    private IBuyerService buyerService;

    @Autowired
    private IWareHouseService wareHouseService;

    @Autowired
    private IPaymentService paymentService;

    @Override
    public ResponseShoppingCartDto shoppingCart(PurchaseOrderDto orderDto) {
        ResponseShoppingCartDto responseShoppingCartDto = new ResponseShoppingCartDto();
        PurchaseOrder purchaseOrder = new PurchaseOrder();

        ShoppingCart shoppingCart = currentShoppingCart(orderDto.getShoopingCartId(), orderDto.getBuyerId());
        if (shoppingCart.getId() > 0) isCloseShoppingCart(shoppingCart.getId());

        List<ProductShoppingCart> productShoppingCarts = new ArrayList<>();

        double totalPrice = shoppingCart.getTotalValue();

        //Trata todos os produtos que foi estão na lista de do carrinho de compra
        for (ProductPurchaseOrderDto p : orderDto.getProducts()) {
            ProductShoppingCart productShoopingCart = new ProductShoppingCart();

            //verifica se tem o produto em estoque disponível
            WareHouseProduct wareHouseProduct = productService.findByProductId(p.getProductId());
            if (p.getQuantity() > wareHouseProduct.getQuantity()) {
                throw new BadRequestExceptionImp("Product quantity " + wareHouseProduct.getProduct().getName() + " is insufficient stock");
            }

            //atualiza o valor total do carrinho
            totalPrice += wareHouseProduct.getProduct().getPrice() * p.getQuantity();

            //itera lista de "produtos de carrinho" e compras
            productShoopingCart.setProductQuantity(p.getQuantity());
            productShoopingCart.setProduct(wareHouseProduct.getProduct());
            productShoopingCart.setShoppingCart(shoppingCart);
            productShoppingCarts.add(productShoopingCart);
        }

        shoppingCart.setTotalValue(totalPrice);
        responseShoppingCartDto.setTotalPrice(totalPrice);

        if (orderDto.getShoopingCartId() == 0){
            // define o status do carrinho como aberto até finalizar a compra e o adiciona à purchase order
            purchaseOrder.setStatusOrder(OrderStatus.OPENED);
            purchaseOrder.setShoppingCart(shoppingCart);
            purchaseOrderRepo.save(purchaseOrder);
        }

        shoppingCartRepo.save(shoppingCart);
        productShoppingCartRepo.saveAll(productShoppingCarts);

        return responseShoppingCartDto;
    }

    @Override
    public ShoppingCart findShoppingCartProductsById(long id) {
        Optional<ShoppingCart> shoppingCart = shoppingCartRepo.findById(id);

        if (shoppingCart.isEmpty()) {
            throw new BadRequestExceptionImp("Shopping cart dont exist");
        }

        return shoppingCart.get();
    }

    private PurchaseOrder findPurchase(long orderId){
        Optional<PurchaseOrder> purchaseOrder = purchaseOrderRepo.findById(orderId);

        if (purchaseOrder.isEmpty()) {
            throw new BadRequestExceptionImp("Purchase Order dont exist");
        }
        if (purchaseOrder.get().getStatusOrder() == OrderStatus.CLOSED) {
            throw new BadRequestExceptionImp("Purchase Order Status is already closed");
        }

        return purchaseOrder.get();
    }

    @Override
    @Transactional
    public ResponsePaymentDTO paymentShoppingCart(CreditCardDTO creditCardDTO, long orderId) {
        PurchaseOrder purchaseOrder = findPurchase(orderId);

        CreditCard creditCard = paymentService.findCreditCardByNumber(creditCardDTO.getCardNumber());

        if (creditCard.getBuyer().getId() != purchaseOrder.getShoppingCart().getBuyer().getId()) {
            throw new UnauthorizedExceptionImp("Buyer unauthorizedException");
        }

        if (!creditCard.getPass().equals(creditCardDTO.getCardPass())){
            throw new UnauthorizedExceptionImp("Buyer unauthorizedException");
        }

        if (creditCard.getCvc() != creditCardDTO.getCardCvc()) {
            throw new UnauthorizedExceptionImp("Buyer unauthorizedException");
        }

        CreditCardPayment creditCardPayment = CreditCardPayment.builder()
                .amountOfInstallment(creditCardDTO.getNumberOfInstallments())
                .purchaseOrder(purchaseOrder)
                .creditCard(creditCard)
                .build();

        creditCardPayment.setInstallmentValue(
                (
                        purchaseOrder.getShoppingCart().getTotalValue() *
                        (
                                1 + (creditCardDTO.getNumberOfInstallments() / 100)
                        )
                ) / creditCardDTO.getNumberOfInstallments()
        );
        paymentService.saveCreditCardPayment(creditCardPayment);

        closeShoppingCart(purchaseOrder);

        return new ResponsePaymentDTO(purchaseOrder, new PaymentDTO(creditCardPayment));
    }

    @Override
    @Transactional
    public ResponsePaymentDTO paymentShoppingCart(PixDTO pixDTO, long orderId) {
        PurchaseOrder purchaseOrder = findPurchase(orderId);
        PixPayment pix = new PixPayment(pixDTO.getPixKey(), purchaseOrder);

        paymentService.savePixPayment(pix);

        closeShoppingCart(purchaseOrder);

        return new ResponsePaymentDTO(purchaseOrder, new PaymentDTO(pix));
    }

    private void closeShoppingCart(PurchaseOrder purchaseOrder) {

        purchaseOrder.setStatusOrder(OrderStatus.CLOSED);
        List<ProductShoppingCart> products = purchaseOrder.getShoppingCart().getListOfShoppingProducts();

        //atualiza a quantidade do produto no banco de dados
        for (ProductShoppingCart product : products) {
            WareHouseProduct wareHouseProduct = productService.findByProductId(product.getProduct().getId());

            // validar se o produto ainda possui estoque
            if (product.getProductQuantity() > wareHouseProduct.getQuantity()) {
                throw new BadRequestExceptionImp("Product quantity " + wareHouseProduct.getProduct().getName() + " is insufficient stock");
            }

            //libera espaço no armazem que estava sendo ocupudado pelo produto
            WareHouseCategory wareHouseCategory = wareHouseService.findByWareHouseIdAndCategoryName(
                    wareHouseProduct.getWareHouse().getId(),
                    product.getProduct().getCategory().getId()
            );
            wareHouseCategory.subStorage((int) product.getProductQuantity());
            wareHouseService.update(wareHouseCategory);

            wareHouseProduct.setQuantity((int) (wareHouseProduct.getQuantity() - product.getProductQuantity()));
        }

        purchaseOrderRepo.save(purchaseOrder);
    }

    private ShoppingCart findById(long id){
        Optional<ShoppingCart> sc = shoppingCartRepo.findById(id);
        if (sc.isPresent()){
            return sc.get();
        }
        throw new BadRequestExceptionImp("Not exist Shopping Cart with id: " + id);
    }

    private ShoppingCart currentShoppingCart(long shoppingCartId, long buyerId){
        ShoppingCart shoppingCart;

        if (shoppingCartId > 0) {

            return findById(shoppingCartId);
        }

        shoppingCart = new ShoppingCart();

        Buyer buyer = buyerService.findById(buyerId);
        shoppingCart.setBuyer(buyer);

        return shoppingCart;
    }


    private void isCloseShoppingCart(long shoppingCart){
        PurchaseOrder purchaseOrder = purchaseOrderRepo.findByShoppingCartId(shoppingCart);
        if (purchaseOrder.getStatusOrder() == OrderStatus.CLOSED){
            throw new BadRequestExceptionImp("Shopping cart is closed");
        }
    }
}
