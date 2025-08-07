import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { DollarChartComponent } from './dollar-chart.component';
import { ApiService, DollarPrice } from '../../services/api.service';

describe('DollarChartComponent', () => {
  let component: DollarChartComponent;
  let fixture: ComponentFixture<DollarChartComponent>;
  let apiService: jasmine.SpyObj<ApiService>;

  const mockPrices: DollarPrice[] = [
    {
      id: 1,
      price: 1.35,
      timestamp: '2024-01-08T10:00:00Z'
    },
    {
      id: 2,
      price: 1.36,
      timestamp: '2024-01-09T10:00:00Z'
    }
  ];

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('ApiService', ['getDollarPrices']);
    
    await TestBed.configureTestingModule({
      imports: [DollarChartComponent, HttpClientTestingModule],
      providers: [
        { provide: ApiService, useValue: spy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DollarChartComponent);
    component = fixture.componentInstance;
    apiService = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load data on init', () => {
    // Given
    apiService.getDollarPrices.and.returnValue(of(mockPrices));
    
    // When
    component.ngOnInit();
    
    // Then
    expect(apiService.getDollarPrices).toHaveBeenCalled();
    expect(component.prices).toEqual(mockPrices);
    expect(component.loading).toBeFalse();
    expect(component.error).toBeNull();
  });

  it('should handle error when loading data fails', () => {
    // Given
    const errorMessage = 'Network error';
    apiService.getDollarPrices.and.returnValue(throwError(() => new Error(errorMessage)));
    
    // When
    component.ngOnInit();
    
    // Then
    expect(apiService.getDollarPrices).toHaveBeenCalled();
    expect(component.error).toBe('Failed to load data. Please try again.');
    expect(component.loading).toBeFalse();
  });

  it('should update chart data when prices are loaded', () => {
    // Given
    apiService.getDollarPrices.and.returnValue(of(mockPrices));
    
    // When
    component.ngOnInit();
    
    // Then
    expect(component.lineChartData.labels).toEqual([
      new Date('2024-01-08T10:00:00Z').toLocaleDateString(),
      new Date('2024-01-09T10:00:00Z').toLocaleDateString()
    ]);
    expect(component.lineChartData.datasets[0].data).toEqual([1.35, 1.36]);
  });

  it('should return current rate correctly', () => {
    // Given
    component.prices = mockPrices;
    
    // When
    const currentRate = component.getCurrentRate();
    
    // Then
    expect(currentRate).toBe(1.35);
  });

  it('should return average rate correctly', () => {
    // Given
    component.prices = mockPrices;
    
    // When
    const averageRate = component.getAverageRate();
    
    // Then
    expect(averageRate).toBe(1.355);
  });

  it('should return last updated timestamp', () => {
    // Given
    component.prices = mockPrices;
    
    // When
    const lastUpdated = component.getLastUpdated();
    
    // Then
    expect(lastUpdated).toBe('2024-01-08T10:00:00Z');
  });

  it('should handle empty prices array', () => {
    // Given
    component.prices = [];
    
    // When & Then
    expect(component.getCurrentRate()).toBe(0);
    expect(component.getAverageRate()).toBe(0);
    expect(component.getLastUpdated()).toBe('');
  });

  it('should reload data when retry button is clicked', () => {
    // Given
    component.error = 'Some error';
    apiService.getDollarPrices.and.returnValue(of(mockPrices));
    
    // When
    component.loadData();
    
    // Then
    expect(apiService.getDollarPrices).toHaveBeenCalled();
    expect(component.prices).toEqual(mockPrices);
    expect(component.loading).toBeFalse();
    expect(component.error).toBeNull();
  });

  it('should unsubscribe on destroy', () => {
    // Given
    spyOn(component['subscription'], 'unsubscribe');
    
    // When
    component.ngOnDestroy();
    
    // Then
    expect(component['subscription'].unsubscribe).toHaveBeenCalled();
  });
}); 