function createfigure1(X1, YMatrix1)
%CREATEFIGURE1(X1, YMATRIX1)
%  X1:  vector of x data
%  YMATRIX1:  matrix of y data

%  Auto-generated by MATLAB on 02-Feb-2016 19:23:54

% Create figure
figure1 = figure;

% Create axes
axes1 = axes('Parent',figure1,'YGrid','on','XTick',[0 0.25 0.5 0.75 1],...
    'XGrid','on',...
    'FontSize',11,...
    'FontName','Calibri');
xlim(axes1,[-0.1 1.1]);
ylim(axes1,[0 1]);
box(axes1,'on');
hold(axes1,'all');

% Create multiple lines using matrix input to plot
plot1 = plot(X1,YMatrix1,'Parent',axes1,'Marker','square','LineWidth',2);
set(plot1(1),'MarkerFaceColor',[0 0 1],'MarkerEdgeColor',[0 0 1],...
    'DisplayName','CPWCV');
set(plot1(2),'MarkerFaceColor',[1 0 0],'MarkerEdgeColor',[1 0 0],...
    'Color',[1 0 0],...
    'DisplayName','Star Forest');
set(plot1(3),'MarkerFaceColor',[0 1 1],'MarkerEdgeColor',[0 1 1],...
    'Color',[0 1 1],...
    'DisplayName','Cycle Cover');

% Create xlabel
xlabel('\alpha');

% Create ylabel
ylabel('Combination','FontSize',11,'FontName','Calibri');

% Create title
title('Combination metric / 20 words','FontSize',12,'FontName','Calibri');

% Create legend
legend1 = legend(axes1,'show');
set(legend1,...
    'Position',[0.817071759259259 0.822347066167292 0.0810763888888889 0.0899500624219725]);

